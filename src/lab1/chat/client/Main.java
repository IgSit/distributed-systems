package lab1.chat.client;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
        System.out.println("Client created");
    }
}
