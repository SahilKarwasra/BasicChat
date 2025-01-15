const express = require('express');
const app = express();

const http = require('http')
const server = http.createServer(app)

const PORT  = 3000

const io = require('socket.io')(server, {
    cors: {
        origin: '*', // Allow all origins or specify your client URL
        methods: ['GET', 'POST']
    }
});

server.listen(PORT, () => {
    console.log(`Server running on PORT ${PORT}`);
});

app.get("/", (req,res) => {
    res.send('Server is running');
});

const users = {};

io.on('connection', (socket) => {
    console.log('A user connected:', socket.id);

    socket.on('setUsername', (username) => {
        users[socket.id] = username;
        console.log(`${username} has joined.`);
        io.emit('userJoined', { username }); // Notify all users
    });

    socket.on('sendMessage', (message) => {
        const username = users[socket.id];
        io.emit('receiveMessage', { username, message }); // Broadcast message
    });

    socket.on('disconnect', () => {
        const username = users[socket.id];
        console.log(`${username} disconnected.`);
        delete users[socket.id];
        io.emit('userLeft', { username }); // Notify all users
    });

})