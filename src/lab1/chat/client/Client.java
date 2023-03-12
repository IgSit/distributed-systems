package lab1.chat.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Client extends Thread {
    private static final int SERVER_PORT = 10000;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread tcpReaderThread;
    private Thread udpReaderThread;
    private Thread writerThread;

    public Client() {
        try {
            this.tcpSocket = new Socket("127.0.0.1", SERVER_PORT);
            this.udpSocket = new DatagramSocket(tcpSocket.getLocalPort());
            this.reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            this.writer = new PrintWriter(tcpSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error creating socket.");
        }
    }

    @Override
    public void run() {
        startWriterThread();
        startTcpReaderThread();
        startUdpReaderThread();
    }

    private void startWriterThread() {
        writerThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if (message == null) {
                    continue;
                }
                if (message.endsWith("-U")) {
                    sendUdpMessage(message.substring(0, message.length() - 2));
                } else {
                    sendTcpMessage(message);
                }
            }
        });
        writerThread.start();
    }

    private void startTcpReaderThread() {
        tcpReaderThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("TCP message: " + reader.readLine());
                } catch (IOException e) {
                    System.out.println("Error reading TCP message.");
                    close();
                }
            }
        });
        tcpReaderThread.start();
    }

    private void startUdpReaderThread() {
        udpReaderThread = new Thread(() -> {
            try {
                byte[] receiveBuffer = new byte[1024];
                while (true) {
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    udpSocket.receive(packet);
                    System.out.println("UDP message: " + Arrays.toString(packet.getData()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading datagram");
                close();
            }
        });
        udpReaderThread.start();
    }

    private void sendTcpMessage(String message) {
        writer.println(message);
    }

    private void sendUdpMessage(String message) {
        try {
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    InetAddress.getByName("127.0.0.1"),
                    SERVER_PORT
            );
            udpSocket.send(packet);
        }  catch (IOException e) {
            System.out.println("Error sending datagram.");
            close();
        }
    }

    private void close() {
        tcpReaderThread.interrupt();
        udpReaderThread.interrupt();
        writerThread.interrupt();
        this.interrupt();
    }
}
