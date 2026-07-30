package org.example;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UdpSender {


    private Thread streamThread;

    private volatile boolean streaming;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> streamTask;

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


    public synchronized void startStream(Config config) {

        if (streaming) {
            return;
        }

        streaming = true;

        // önceden user-thread açılıyordu. şimdi Daemon thread açılıyor bu sayede pencere kapanınca jvm de kapanıyor.

        scheduler = Executors.newSingleThreadScheduledExecutor(r ->{

                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setName("udp-stream");
                    return  t;
                });

        streamTask = scheduler.scheduleAtFixedRate(
                () -> send(config),
                0,
                config.getTimer().getIntervalMs(),
                TimeUnit.MILLISECONDS
        );
    }

    public synchronized void stopStream() {

        if (!streaming) {
            return;
        }

        streaming = false;

        if (streamTask != null) {
            streamTask.cancel(false);
            streamTask = null;
        }

        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }



    public byte[] hexStringToByteArray(String hex){


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
