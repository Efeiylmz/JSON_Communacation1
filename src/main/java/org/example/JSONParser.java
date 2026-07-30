package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JSONParser {

    private static final File CONFIG_FILE = new File("config.json");

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public Config getJSONdata() throws ConfigException {

        try {
            // 1) Önce çalışma dizinindeki dosyaya bak
            if (CONFIG_FILE.exists() && CONFIG_FILE.length() > 0) {
                return MAPPER.readValue(CONFIG_FILE, Config.class);
            }

            // 2) Yoksa jar/classpath içindeki varsayılana düş
            try (InputStream in = JSONParser.class.getResourceAsStream("/config.json")) {

                if (in == null) {
                    throw new ConfigException(
                            "config.json bulunamadı: " + CONFIG_FILE.getAbsolutePath());
                }

                return MAPPER.readValue(in, Config.class);
            }

        } catch (JsonProcessingException e) {
            throw new ConfigException(
                    "config.json bozuk (satır " + e.getLocation().getLineNr() + "): "
                            + e.getOriginalMessage(), e);

        } catch (IOException e) {
            throw new ConfigException(
                    "config.json okunamadı: " + e.getMessage(), e);
        }
    }

    public void saveJSONData(Config config) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(CONFIG_FILE, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
