package lab1.zad1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String[] args) {
        System.out.println("JAVA UDP SERVER");

        int portNumber = 9008;
        byte[] receiveBuffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(portNumber)) {
            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                InetAddress clientAddress = receivePacket.getAddress();
                System.out.println("Received msg: " + msg + "\nfrom address: " + clientAddress);
                // reply
                byte[] sendBuffer = "Reply from server".getBytes();
                int clientPort = receivePacket.getPort();
                DatagramPacket replyPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(replyPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
