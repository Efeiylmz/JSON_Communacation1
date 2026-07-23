package org.example;


import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender {


    private Thread streamThread;

    private volatile boolean streaming;






    public void send(Config config){

        try(DatagramSocket socket = createSocket()){

            DatagramPacket packet = createPacket(config);

            socket.send(packet);

            System.out.println("Packet sent.");

        }   catch (Exception e) {
            e.printStackTrace();
            }
    }

    private DatagramPacket createPacket(Config config) throws Exception {

        byte[] data =
                hexStringToByteArray(config.getMessage().getText());

        InetAddress address =
                InetAddress.getByName(config.getNetwork().getIp());

        return new DatagramPacket(
                data,
                data.length,
                address,
                config.getNetwork().getPort());

    }

    private DatagramSocket createSocket() throws Exception {

        return new DatagramSocket();

    }


    public void startStream(Config config) {

        if (streaming) {
            return;
        }

        streaming = true;

        streamThread = new Thread(() -> {

            while (streaming) {

                send(config);

                try {

                    Thread.sleep(config.gettimer().getIntervalMs());

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;

                }
//                scheduledexecutorservice
            }

        });

        streamThread.setDaemon(true);
        streamThread.start();
    }

    public void stopStream() {

        streaming = false;

        if (streamThread != null) {
            streamThread.interrupt();
        }
    }



    private byte[] hexStringToByteArray(String hex){


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
