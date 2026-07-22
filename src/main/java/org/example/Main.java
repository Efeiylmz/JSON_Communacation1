package org.example;
import java.io.InputStream;


import static javafx.application.Application.launch;

public class Main {

    public static void main(String[] args) {

        JSONParser jsonparser = new JSONParser();

        Config config = jsonparser.getJSONdata();

        // OKUNAN BİLGİLERİ AYRI FİLEDLARDAN GETİR
            System.out.println("IP      : " + config.getNetwork().getIp());
            System.out.println("Port    : " + config.getNetwork().getPort());
            System.out.println("Message : " + config.getMessage().getText());
            System.out.println("Interval Ms : " + config.gettimer().getIntervalMs());

            UdpSender sender = new UdpSender();
            sender.send(config);

            launch(HelloApplication.class, args);
        }
    }
