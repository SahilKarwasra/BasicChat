# Basic Group Chat App

A **Basic Group Chat App** built using **Jetpack Compose** for the frontend and **Node.js** with **Socket.IO** for the backend. This project is a part of my learning journey to understand and integrate real-time communication using Socket.IO in Node.js and connect it to an Android frontend.

---

## Features

- **Real-Time Group Chat**: Allows multiple users to join and chat in real time.
- **User-Friendly UI**: Built with Jetpack Compose for a modern and responsive interface.
- **Socket.IO Integration**: Utilizes Socket.IO for seamless real-time communication.
- **Backend with Node.js**: A lightweight and efficient backend for handling WebSocket connections.

---

## Tech Stack

### Frontend:
- **Jetpack Compose**: Modern UI toolkit for building native Android UIs.
- **Kotlin**: Programming language for Android development.

### Backend:
- **Node.js**: JavaScript runtime for server-side development.
- **Socket.IO**: Library for real-time, bi-directional communication.

---

## How It Works

1. **Server Setup**:
   - The Node.js backend uses Socket.IO to handle WebSocket connections.
   - Clients can connect, send messages, and receive updates in real time.

2. **Client Integration**:
   - The Jetpack Compose frontend connects to the server using a WebSocket client.
   - Messages are displayed in a chat interface and updated dynamically.

---

## Installation and Setup

### Backend:
1. Clone the repository:
   ```bash
   git clone https://github.com/SahilKarwasra/BasicChat.git
   cd app/src/main/java/com/example/basicchat/BaiscChatAppServer
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the server:
   ```bash
   node server.js
   ```

### Frontend:
1. Open the Android project in **Android Studio**.
2. Update the WebSocket server URL in your code (if required).
3. Build and run the app on an emulator or a physical device.

---

## Learning Outcomes

- Understanding **Socket.IO** and its real-time communication capabilities.
- Building a **Node.js** server to manage WebSocket connections.
- Integrating a WebSocket client in an **Android app** using **Jetpack Compose**.

---

## Future Improvements

- Add user authentication.
- Support for private chats.
- Enhance UI/UX with additional features like message timestamps, typing indicators, etc.

---

## License
This project is open-source and available under the [MIT License](LICENSE).

---

## Acknowledgments

- [Socket.IO Documentation](https://socket.io/docs/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

---

Feel free to contribute or provide feedback!
