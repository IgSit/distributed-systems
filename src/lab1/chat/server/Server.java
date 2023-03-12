package lab1.chat.server;

import lab1.chat.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server extends Thread {
    private static final int PORT = 10000;
    private final Map<UUID, PrintWriter> tcpConnectionsMap = new HashMap<>();
    private ServerSocket socket;

    public Server() {
        try {
            this.socket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Error creating socket");
        }
    }

    @Override
    public void run() {
        Thread initThread = new Thread(() -> {
            while (true) {
                establishNewConnection();
            }
        });
        initThread.start();
    }

    public void sendTcp(Message message) {
        System.out.println(message);
        for (UUID clientUuid : tcpConnectionsMap.keySet()) {
            if (clientUuid != message.senderUuid()) {
                PrintWriter clientWriter = tcpConnectionsMap.get(clientUuid);
                clientWriter.println(message);
            }
        }
    }

    public synchronized void removeConnection(UUID uuid) {
        PrintWriter writer = tcpConnectionsMap.get(uuid);
        writer.close();
        tcpConnectionsMap.remove(uuid);
    }

    private void establishNewConnection() {
        try {
            Socket clientSocket = socket.accept();
            ConnectionThread connectionThread = new ConnectionThread(this, clientSocket);
            addNewConnection(clientSocket, connectionThread.getUuid());
            connectionThread.start();
        } catch (IOException e) {
            System.out.println("Can't establish connection.");
        }
    }

    private synchronized void addNewConnection(Socket clientSocket, UUID uuid) throws IOException {
        PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        tcpConnectionsMap.put(uuid, clientWriter);
    }
}
