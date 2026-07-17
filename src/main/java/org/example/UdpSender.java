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

            // ip, port, message değerleri alınır

            byte[] data = hexStringtoByteArray(message);

            // hex byte dönüşümlü veri diziye kaydedilir


            InetAddress adress = InetAddress.getByName(ip);

            DatagramSocket socket = new DatagramSocket();

            DatagramPacket packet = new DatagramPacket(data , data.length , adress , port );

            socket.send(packet);

            System.out.println("Paket Gönderildi.....");
        }   catch (Exception e) {
            e.printStackTrace();

            }
    }



    private byte[] hexStringtoByteArray(String hex){


        // Eğer boşluklu gelirse kaldır
        hex = hex.replace(" ","");

        if (hex.length() %2 != 0 ) {

            throw new IllegalArgumentException(" Hex string lenghth must be even number ");
        }

        byte[] data = new byte[hex.length()/2];

        for(int i = 0 ; i < hex.length() ; i +=2 ){

            // string hex dönüşümü yapılır

            String byteString = hex.substring(i,i+2);

            data[i/2] = (byte) Integer.parseInt(byteString,16);
        }

        return data;

    }

}
