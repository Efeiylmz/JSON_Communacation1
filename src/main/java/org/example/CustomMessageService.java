package org.example;

import java.util.ArrayList;

public class CustomMessageService {

    private final JSONParser parser = new JSONParser();


    public CustomMessage findByName(Config config, String name) {
        if (config.getCustomMessagesList() == null) return null;
        for (CustomMessage m : config.getCustomMessagesList()) {
            if (name.equals(m.getName())) return m;
        }
        return null;
    }

    public String Update(Config config, CustomMessage message){

        if(config.getCustomMessagesList() == null ){
            config.setCustomMessagesList(new ArrayList<>());
        }

        CustomMessage existing = findByName(config, message.getName());

        if (existing != null ){
            int index = config.getCustomMessagesList().indexOf(existing);
            config.getCustomMessagesList().set(index,message);
        } else{
            config.getCustomMessagesList().add(message);
        }

        String combinedHex = HexUtil.build(message);

        if(config.getMessage() == null ){               // config nesnesinde message kısmı boşsa oluştur

            config.setMessage(new MessageConfig());
        }

        config.getMessage().setText(combinedHex);

        parser.saveJSONData(config);
        return combinedHex;
    }

    public void delete(Config config, CustomMessage message){

        config.getCustomMessagesList().remove(message);

        // silinen mesaj o an aktif gönderilecek metinse (Update() sırasında oraya
        // kopyalanmıştı), eski/stale hex'in gönderilmeye devam etmemesi için temizle.

        if (config.getMessage() != null
                && HexUtil.build(message).equals(config.getMessage().getText())) {
            config.getMessage().setText("");
        }

        parser.saveJSONData(config);
    }

}
