package lab1.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private static final int SERVER_PORT = 10000;
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;
    private Thread writerThread;

    public Client() {
        try {
            this.clientSocket = new Socket("127.0.0.1", SERVER_PORT);
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error creating socket.");
        }
    }

    @Override
    public void run() {
        startWriterThread();
        startReaderThread();
    }

    private void startReaderThread() {
        readerThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.println(reader.readLine());
                } catch (IOException e) {
                    System.out.println("Error reading data.");
                    readerThread.interrupt();
                    writerThread.interrupt();
                    this.interrupt();
                }
            }
        });
        readerThread.start();
    }

    private void startWriterThread() {
        writerThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if (message != null) {
                    writer.println(message);
                }
            }
        });
        writerThread.start();
    }
}
