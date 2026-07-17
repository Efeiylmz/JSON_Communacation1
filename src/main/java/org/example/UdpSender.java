package org.example;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender {

    public void send(Config config){

        try{
            String ip = config.getNetwork().getIp();

            int port = config.getNetwork().getPort();

            String message = config.getMessage().getText();

            byte[] data = message.getBytes();

            InetAddress adress = InetAddress.getByName(ip);

            DatagramSocket socket = new DatagramSocket();

            DatagramPacket packet = new DatagramPacket(data , data.length , adress , port );

            socket.send(packet);

            System.out.println("Paket Gönderildi.....");
        }   catch (Exception e) {
            e.printStackTrace();

            }
    }

}
