package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class UdpSenderApplication extends Application {

    @Override
    public void start(Stage stage) throws   IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UdpSenderApplication.class.getResource("/udp-sender-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        URL styleUrl = Objects.requireNonNull(
                UdpSenderApplication.class.getResource("/style.css"),
                "style.css not found on classpath");
        scene.getStylesheets().add(styleUrl.toExternalForm());
        stage.setTitle("UDP Sender");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}