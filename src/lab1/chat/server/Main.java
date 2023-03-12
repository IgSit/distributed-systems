package lab1.chat.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        System.out.println("Server created.");
    }
}
