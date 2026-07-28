package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UDPSenderController {

    private Config config;

    private boolean streaming;

    private final JSONParser parser = new JSONParser();

    private final UdpSender sender = new UdpSender();

    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private TextField messageField;
    @FXML
    private TextField intervalField;
    @FXML
    private Label status;
    @FXML
    private Button sendPeriodicButton;


    private void updateConfigFromGui(){

        config.getNetwork().setIp(ipField.getText());
        config.getNetwork().setPort(Integer.parseInt(portField.getText()));
        config.getMessage().setText(messageField.getText());
        config.getTimer().setIntervalMs(Integer.parseInt(intervalField.getText()));

    }
    @FXML
    private void loadJson(){

        try {
            Config loaded = parser.getJSONdata();

            // Buraya geldiysek yükleme başarılı — ancak şimdi atıyoruz
            config = loaded;

            ipField.setText(config.getNetwork().getIp());
            portField.setText(String.valueOf(config.getNetwork().getPort()));
            messageField.setText(config.getMessage().getText());
            intervalField.setText(String.valueOf(config.getTimer().getIntervalMs()));

            status.setText("Status : JSON settings uploaded.....!");

        } catch (ConfigException e) {
            status.setText("Status : " + e.getMessage());
            e.printStackTrace();   // konsolda tam yığın izi kalsın
        }
    }

    @FXML
    private void saveJson(){

        if (config == null) {
            status.setText("Load a JSON file first.");
            return;
        }

        updateConfigFromGui();
        parser.saveJSONData(config);

        status.setText("Status : New JSON configurations updated.....!");

    }

    @FXML
    private void sendOnce(){

        if (config == null) {
            status.setText("Load a JSON file first.");
            return;
        }

        updateConfigFromGui();
        sender.send(config);
        status.setText("Status : Packet is sent!");

    }
    @FXML
    private void sendPeriodic(){

        if (streaming==false) {

            if (config == null) {
                status.setText("Load a JSON file first.");
                return;
            }

            updateConfigFromGui();

            streaming = true;
            sendPeriodicButton.setText("STOP");
            sender.startStream(config);

        }else{

            streaming=false;
            sendPeriodicButton.setText("Send Periodically");
            sender.stopStream();
            status.setText("Streaming STOPPED...");

        }

    }

    @FXML
    private void newMessageWindow() throws IOException {

        if (config == null) {
            status.setText("Önce JSON yükleyin.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                UdpSenderApplication.class.getResource("/new-message-view.fxml"));
        Scene scene = new Scene(loader.load());   // load() önce çağrılmalı

        newMessageController controller = loader.getController();
        controller.setConfig(config);                      // aynı nesne paylaşılıyor

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }


}
