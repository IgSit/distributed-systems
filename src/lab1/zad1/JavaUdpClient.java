package lab1.zad1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpClient {

    public static void main(String[] args) {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
        int portNumber = 9008;
        byte[] receiveBuffer = new byte[1024];
        Arrays.fill(receiveBuffer, (byte) 0);

        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            byte[] sendBuffer = "Client UDP msg".getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            System.out.println(new String(receivePacket.getData()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
