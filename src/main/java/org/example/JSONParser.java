package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;



public class JSONParser {

        public Config getJSONdata(){

        try {

            // JSON dosyasını resources klasöründen oku
            InputStream input = Main.class.getResourceAsStream("/config.json");

            if (input == null) {
                System.out.println("config.json dosyası bulunamadı.");
                return null;
            }

            // JSON'u Config nesnesine dönüştür

            ObjectMapper mapper = new ObjectMapper();
            Config configuration = mapper.readValue(input, Config.class);
            return configuration;

        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }

        public void saveJSONData(Config config){

            try {

                ObjectMapper mapper = new ObjectMapper();

                File file = new File("src/main/resources/config.json");

                mapper.writerWithDefaultPrettyPrinter().writeValue(file, config);

            } catch (Exception e) {

                e.printStackTrace();

            }
        }




}