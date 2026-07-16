package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        try {

            // JSON dosyasını resources klasöründen oku
            InputStream input = Main.class.getResourceAsStream("/config.json");

            if (input == null) {
                System.out.println("config.json dosyası bulunamadı.");
                return;
            }

            // JSON'u Config nesnesine dönüştür

            ObjectMapper mapper = new ObjectMapper();
            Config config = mapper.readValue(input, Config.class);

            // Okunan bilgileri ekrana yazdır

            System.out.println("IP      : " + config.getIp());
            System.out.println("Port    : " + config.getPort());
            System.out.println("Message : " + config.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}