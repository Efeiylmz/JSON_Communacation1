package org.example;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UdpSender {


    private Thread streamThread;

    private volatile boolean streaming;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> streamTask;

    public void send(Config config) throws IOException {

        try(DatagramSocket socket = createSocket()){

            DatagramPacket packet = createPacket(config);

            socket.send(packet);

            System.out.println("Packet sent.");

        }
    }

    private DatagramPacket createPacket(Config config)
            throws UnknownHostException {

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

    // Paketi gerçekten göndermeden hazırlamayı dener; mesaj/hex ya da IP geçersizse
    // aynı exception'ları (IllegalArgumentException, UnknownHostException) fırlatır.
    // sendOnce() ve startStream() ikisi de göndermeden/akışı başlatmadan önce bunu
    // çağırır; böylece send() ve periyodik görev sadece "geçerliliği bilinen bir
    // paketi oluştur ve gönder" işine odaklanır.
    public void validate(Config config) throws IOException {
        createPacket(config);
    }

    private DatagramSocket createSocket() throws SocketException {
        return new DatagramSocket();
    }


    public synchronized void startStream(Config config) {
        startStream(config, null);
    }

    // onError: periyodik gönderim sırasında bir hata olursa (bozuk hex, geçersiz IP, IO hatası...)
    // çağrılır. Arka plan thread'inden çağrıldığı için UI tarafı bunu FX thread'ine taşımalı.
    public synchronized void startStream(Config config, java.util.function.Consumer<String> onError) {

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

        streamTask = scheduler.scheduleAtFixedRate(() -> {

                    try {
                        send(config);
                    } catch (Exception e) {
                        // startStream() öncesi validate() geçtiği için normalde buraya
                        // düşülmez; yine de gerçek bir I/O sorunu (ör. ağ o an ulaşılamaz
                        // olursa) ya da beklenmedik bir durum için güvenlik ağı olarak kalıyor.
                        stopStream();
                        if (onError != null) {
                            onError.accept(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
                        }
                    }

                }, 0,
                config.getTimer().getIntervalMs(),
                TimeUnit.MILLISECONDS);
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