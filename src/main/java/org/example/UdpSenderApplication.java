package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class UdpSenderApplication extends Application {

    @Override
    public void start(Stage stage) throws   IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UdpSenderApplication.class.getResource("/udp-sender-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(),500,400);
        stage.setTitle("UDP Sender");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();



    }
}