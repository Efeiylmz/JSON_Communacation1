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
import java.net.SocketException;
import java.net.UnknownHostException;

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

        // guideki fieldlara yazılı değerleri config nesnesine kaydediyor.
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
        try {
            updateConfigFromGui();
        } catch (NumberFormatException e) {
            status.setText("Invalid port or interval value.");
            return;
        }
        parser.saveJSONData(config);
        status.setText("Status : New JSON configurations updated.....!");
    }

    @FXML
    private void sendOnce() {

        if (config == null) {
            status.setText("Load a JSON file first.");
            return;
        }

        try {

            updateConfigFromGui();
            sender.send(config);

            status.setText("Packet sent.");

        } catch (UnknownHostException e) {

            status.setText("Invalid IP address.");

        } catch (SocketException e) {

            status.setText("Socket could not be created.");

        } catch (IllegalArgumentException e) {

            status.setText(e.getMessage());

        } catch (IOException e) {

            status.setText("Packet could not be sent.");
        }
    }

    @FXML
    private void sendPeriodic(){

        if (streaming==false) {
            if (config == null) {
                status.setText("Load a JSON file first.");
                return;
            }
            try {
                updateConfigFromGui();
            } catch (NumberFormatException e) {
                status.setText("Invalid port or interval value.");
                return;
            }
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
            status.setText("Load a JSON file first.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                UdpSenderApplication.class.getResource("/new-message-view.fxml"));
        Scene scene = new Scene(loader.load());   // load() önce çağrılmalı

        NewMessageController controller = loader.getController();
        controller.setConfig(config);                      // aynı nesne paylaşılıyor

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();

        // pencere kapanırken senkronize et (garanti olsun diye)
        if(config.getMessage() != null){
            messageField.setText(config.getMessage().getText());
    }

    }


}
