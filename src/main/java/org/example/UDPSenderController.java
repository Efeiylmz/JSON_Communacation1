package org.example;

import javafx.application.Platform;
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

    // status etiketini metinle birlikte başarı(yeşil)/hata(kırmızı) rengine boyar
    private void setStatus(String message, boolean isError) {
        status.setText(message);
        status.getStyleClass().removeAll("status-success", "status-error");
        if (message != null && !message.isBlank()) {
            status.getStyleClass().add(isError ? "status-error" : "status-success");
        }
    }

    // akış aktifken buton kırmızı (STOP), aktif değilken mavi (primary) görünür
    private void setStreamButtonActive(boolean active) {
        sendPeriodicButton.getStyleClass().removeAll("button-primary", "button-danger");
        sendPeriodicButton.getStyleClass().add(active ? "button-danger" : "button-primary");
    }

    // config'in gönderilebilir olup olmadığını kontrol eder (hex geçerli mi, IP çözülebiliyor mu);
    // geçersizse status'u kırmızı hata mesajıyla günceller ve false döner. sendOnce() ve
    // sendPeriodic() ikisi de göndermeden/akışı başlatmadan önce bunu çağırıyor.
    private boolean validateConfigForSend() {
        try {
            sender.validate(config);
            return true;
        } catch (UnknownHostException e) {
            setStatus("Invalid IP address.", true);
        } catch (IllegalArgumentException e) {
            setStatus(e.getMessage(), true);
        } catch (IOException e) {
            setStatus("Packet could not be prepared: " + e.getMessage(), true);
        }
        return false;
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

            setStatus("Status : JSON settings uploaded.....!", false);

        } catch (ConfigException e) {
            setStatus("Status : " + e.getMessage(), true);
            e.printStackTrace();   // konsolda tam yığın izi kalsın
        }
    }

    @FXML
    private void saveJson(){
        if (config == null) {
            setStatus("Load a JSON file first.", true);
            return;
        }
        try {
            updateConfigFromGui();
        } catch (NumberFormatException e) {
            setStatus("Invalid port or interval value.", true);
            return;
        }
        parser.saveJSONData(config);
        setStatus("Status : New JSON configurations updated.....!", false);
    }

    @FXML
    private void sendOnce() {

        if (config == null) {
            setStatus("Load a JSON file first.", true);
            return;
        }

        try {
            updateConfigFromGui();
        } catch (NumberFormatException e) {
            setStatus("Invalid port or interval value.", true);
            return;
        }

        if (!validateConfigForSend()) {
            return;
        }

        try {
            sender.send(config);
            setStatus("Packet sent.", false);
        } catch (SocketException e) {
            setStatus("Socket could not be created.", true);
        } catch (IOException e) {
            setStatus("Packet could not be sent.", true);
        }
    }

    @FXML
    private void sendPeriodic(){

        if (streaming==false) {
            if (config == null) {
                setStatus("Load a JSON file first.", true);
                return;
            }
            try {
                updateConfigFromGui();
            } catch (NumberFormatException e) {
                setStatus("Invalid port or interval value.", true);
                return;
            }

            if (!validateConfigForSend()) {
                return;
            }

            streaming = true;
            sendPeriodicButton.setText("STOP");
            setStreamButtonActive(true);
            sender.startStream(config, this::handleStreamError);
            setStatus("Streaming STARTED...", false);
        }else{

            streaming=false;
            sendPeriodicButton.setText("Send Periodically");
            setStreamButtonActive(false);
            sender.stopStream();
            setStatus("Streaming STOPPED...", false);

        }

    }

    // sender.startStream() sırasında arka plan thread'inde bir hata olursa çağrılır.
    // JavaFX kontrollerine sadece FX Application Thread'inden dokunulabildiği için
    // Platform.runLater ile UI güncellemesi ana thread'e taşınıyor.
    private void handleStreamError(String message) {
        Platform.runLater(() -> {
            streaming = false;
            sendPeriodicButton.setText("Send Periodically");
            setStreamButtonActive(false);
            setStatus("Streaming stopped: " + message, true);
        });
    }

    @FXML
    private void newMessageWindow() throws IOException {

        if (config == null) {
            setStatus("Load a JSON file first.", true);
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                UdpSenderApplication.class.getResource("/new-message-view.fxml"));
        Scene scene = new Scene(loader.load());   // load() önce çağrılmalı
        scene.getStylesheets().add(UdpSenderApplication.class.getResource("/style.css").toExternalForm());

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