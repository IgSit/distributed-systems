package lab1.zad3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");

        int portNumber = 9008;
        try (DatagramSocket socket = new DatagramSocket(portNumber)) {
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                changeEndianness(receiveBuffer);
                int num = ByteBuffer.wrap(receiveBuffer).getInt();
                System.out.println("Received number: " + num);
                num++;
                byte[] sendBuffer = ByteBuffer.allocate(4).putInt(num).array();
                changeEndianness(sendBuffer);
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket replyPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(replyPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeEndianness(byte[] msg) {
        for (int byteStart = 0; byteStart < msg.length; byteStart = byteStart + 4) {
            swap(msg, byteStart, byteStart+3);
            swap(msg, byteStart+1, byteStart+2);
        }
    }

    public static void swap(byte[] arr, int i, int j) {
        byte tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
