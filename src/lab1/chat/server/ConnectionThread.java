package lab1.chat.server;

import lab1.chat.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

public class ConnectionThread extends Thread {
    private final UUID uuid;
    private final Server server;
    private final BufferedReader reader;

    public ConnectionThread(Server server, Socket clientSocket) {
        try {
            this.uuid = UUID.randomUUID();
            this.server = server;
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = new Message(uuid, reader.readLine());
                server.sendTcp(message);
            } catch (IOException e) {
                System.out.println("Error while reading data.");
                server.removeConnection(uuid);
                this.interrupt();
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }
}
