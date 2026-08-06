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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;

public class UDPSenderController {

    private static final System.Logger LOGGER = System.getLogger(UDPSenderController.class.getName());

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
    @FXML
    private Button sendOnceButton;
    @FXML
    private Button loadJsonButton;
    @FXML
    private Button saveJsonButton;
    @FXML
    private Button showMessagesButton;

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

    // akış çalışırken, o an gönderilmekte olan config ile çelişebilecek her şeyi kilitler:
    // alan düzenlemeleri stream yeniden başlamadan uygulanmaz, Send Once aynı anda ikinci
    // bir gönderimle çakışır, Load JSON çalışan stream'in referans aldığı config'i arkadan
    // değiştirir, Show Messages ise aynı config nesnesini paylaştığı için mesajı akış
    // sürerken sessizce değiştirebilir. sendPeriodicButton (STOP) her zaman açık kalır.
    private void setControlsDisabled(boolean disabled) {
        ipField.setDisable(disabled);
        portField.setDisable(disabled);
        messageField.setDisable(disabled);
        intervalField.setDisable(disabled);
        sendOnceButton.setDisable(disabled);
        loadJsonButton.setDisable(disabled);
        saveJsonButton.setDisable(disabled);
        showMessagesButton.setDisable(disabled);
    }

    // config'in gönderilebilir olup olmadığını kontrol eder (hex geçerli mi, IP çözülebiliyor mu);
    // geçersizse status'u kırmızı hata mesajıyla günceller ve true döner. sendOnce() ve
    // sendPeriodic() ikisi de göndermeden/akışı başlatmadan önce bunu çağırıyor.
    private boolean configInvalidForSend() {
        try {
            sender.validate(config);
            return false;
        } catch (UnknownHostException e) {
            setStatus("Invalid IP address.", true);
        } catch (IllegalArgumentException e) {
            setStatus(e.getMessage(), true);
        } catch (IOException e) {
            setStatus("Packet could not be prepared: " + e.getMessage(), true);
        }
        return true;
    }


    @FXML
    private void loadJson(){

        try {
            config = parser.getJSONdata();

            ipField.setText(config.getNetwork().getIp());
            portField.setText(String.valueOf(config.getNetwork().getPort()));
            messageField.setText(config.getMessage().getText());
            intervalField.setText(String.valueOf(config.getTimer().getIntervalMs()));

            setStatus("Status : JSON settings uploaded.....!", false);

        } catch (ConfigException e) {
            setStatus("Status : " + e.getMessage(), true);
            LOGGER.log(System.Logger.Level.ERROR, "Failed to load config.json", e);
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

        if (configInvalidForSend()) {
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

        if (!streaming) {
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

            if (configInvalidForSend()) {
                return;
            }

            streaming = true;
            sendPeriodicButton.setText("STOP");
            setStreamButtonActive(true);
            setControlsDisabled(true);
            sender.startStream(config, this::handleStreamError);
            setStatus("Streaming STARTED...", false);
        }else{

            streaming=false;
            sendPeriodicButton.setText("Send Periodically");
            setStreamButtonActive(false);
            setControlsDisabled(false);
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
            setControlsDisabled(false);
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

        URL styleUrl = Objects.requireNonNull(
                UdpSenderApplication.class.getResource("/style.css"),
                "style.css not found on classpath");
        scene.getStylesheets().add(styleUrl.toExternalForm());

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