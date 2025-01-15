package com.example.basicchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basicchat.ui.theme.BasicChatTheme
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicChatTheme {
                SocketHandler.setSocket()
                val mSocket = SocketHandler.getSocket()
                SocketHandler.establishConnection()
                ChatApp(socket = mSocket)
            }
        }
    }
}
@Composable
fun ChatApp(socket: Socket) {
    var username by remember { mutableStateOf<String?>(null) }

    if (username == null) {
        LoginScreen { enteredUsername ->
            username = enteredUsername
            // Send username to the server after login
            socket.emit("setUsername", enteredUsername)
        }
    } else {
        ChatScreen(username = username!!, socket = socket)
    }
}

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var username by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your username",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (username.isNotBlank()) {
                        onLogin(username)
                    }
                }
            ) {
                Text("Join Chat")
            }
        }
    }
}

@Composable
fun MessageBubble(message: String, isOutgoing: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isOutgoing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isOutgoing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun MessagesList(messages: List<Pair<String, Boolean>>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        messages.forEach { (message, isOutgoing) ->
            MessageBubble(message = message, isOutgoing = isOutgoing)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(username: String, socket: Socket) {
    val messages = remember { mutableStateListOf<Pair<String, Boolean>>() }

    DisposableEffect(socket) {
        onDispose {
            SocketHandler.closeConnection()
        }
    }

    LaunchedEffect(socket) {
        socket.on("receiveMessage") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val message = data.getString("message")
                val sender = data.getString("username")
                val isOutgoing = sender == username
                messages.add("$sender: $message" to isOutgoing)
            }
        }

        socket.on("userJoined") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val newUser = data.getString("username")
                messages.add("$newUser joined the chat." to false)
            }
        }

        socket.on("userLeft") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val leftUser = data.getString("username")
                messages.add("$leftUser left the chat." to false)
            }
        }
    }
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(title = { Text("ChatApp") })
        },
        content = { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                MessagesList(messages = messages, modifier = Modifier.weight(1f))
                MessageInputField(
                    onSend = { text ->
                        socket.emit("sendMessage", text)
                    }
                )
            }
        }
    )
}

@Composable
fun MessageInputField(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp),
            textStyle = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }
        ) {
            Text("Send")
        }
    }
}

object SocketHandler {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            Log.d("SocketHandler", "Initializing socket connection...")
            mSocket = IO.socket("http://10.0.2.2:3000")
        } catch (e: URISyntaxException) {
            Log.e("SocketHandler", "Socket initialization error: ${e.message}")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        if (!mSocket.connected()) {
            Log.d("SocketHandler", "Connecting to socket...")
            mSocket.connect()
        }
    }

    @Synchronized
    fun closeConnection() {
        if (mSocket.connected()) {
            mSocket.disconnect()
        }
    }
}