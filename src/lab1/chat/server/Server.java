package lab1.chat.server;

import lab1.chat.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Server extends Thread {
    private static final int PORT = 10000;
    private final Map<Integer, PrintWriter> connectionsMap = new HashMap<>();
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private Thread tcpInitThread;
    private Thread udpThread;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Error creating socket");
        }
    }

    @Override
    public void run() {
        startTcpInitThread();
        startUdpThread();
    }

    private void sendUdpMessage(DatagramPacket senderPacket) {
        System.out.println("UDP message arrived: " + Arrays.toString(senderPacket.getData()));
        for (int clientPort : connectionsMap.keySet()) {
            if (clientPort != senderPacket.getPort()) {
                try {
                    DatagramPacket packet = new DatagramPacket(
                            senderPacket.getData(),
                            senderPacket.getLength(),
                            InetAddress.getByName("127.0.0.1"),
                            clientPort
                    );
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    System.out.println("Can't send datagram.");
                }
            }
        }
    }

    private void sendTcpMessage(Message message) {
        System.out.println("TCP message arrived: " + message);
        for (int clientPort : connectionsMap.keySet()) {
            if (clientPort != message.senderPort()) {
                PrintWriter clientWriter = connectionsMap.get(clientPort);
                clientWriter.println(message);
            }
        }
    }

    private void startTcpInitThread() {
        tcpInitThread = new Thread(() -> {
            while (true) {
                establishNewTcpConnection();
            }
        });
        tcpInitThread.start();
    }

    private void establishNewTcpConnection() {
        try {
            Socket clientSocket = serverSocket.accept();
            addNewTcpConnection(clientSocket);
            maintainTcpConnection(clientSocket);
        } catch (IOException e) {
            System.out.println("Can't establish connection.");
            close();
        }
    }

    private synchronized void addNewTcpConnection(Socket clientSocket) throws IOException {
        PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        connectionsMap.put(clientSocket.getPort(), clientWriter);
    }

    private void maintainTcpConnection(Socket clientSocket) {
        Thread connectionThread = new Thread(() -> {
            int clientPort = clientSocket.getPort();
            BufferedReader reader = initReader(clientSocket);
            while (true) {
                try {
                    assert reader != null;
                    Message message = new Message(clientPort, reader.readLine());
                    sendTcpMessage(message);
                } catch (IOException e) {
                    System.out.println("Error while reading data. Closing connection.");
                    removeTcpConnection(clientPort);
                    this.interrupt();
                    break;
                }
            }
        });
        connectionThread.start();
    }

    private synchronized void removeTcpConnection(int clientPort) {
        connectionsMap.remove(clientPort);
    }

    private BufferedReader initReader(Socket clientSocket) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Can't init reader.");
            return null;
        }
        return reader;
    }

    private void startUdpThread() {
        udpThread = new Thread(() -> {
            try  {
                datagramSocket = new DatagramSocket(PORT);
                byte[] receiveBuffer = new byte[1024];
                while (true) {
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    datagramSocket.receive(packet);
                    sendUdpMessage(packet);
                }
            } catch (IOException e) {
                System.out.println("Can't create UDP socket.");
                close();
            }
        });
        udpThread.start();
    }

    private void close() {
        tcpInitThread.interrupt();
        udpThread.interrupt();
    }
}
