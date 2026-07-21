package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private Config config;

    @Override
    public void start(Stage stage) {

        NetworkConfig network = new NetworkConfig();
        MessageConfig message = new MessageConfig();
        JSONParser parser = new JSONParser();
        UdpSender sender = new UdpSender();



        VBox root = new VBox(20);

        Label title = new Label("UDP Packet Sender");

        Label status = new Label("Status :");

        Label ipLabel = new Label("IP Label");
        TextField ipField = new TextField();
        ipField.setPromptText("111.111.111.111");

        Label portLabel = new Label("Port Label");
        TextField portField = new TextField();
        portField.setPromptText("12345");

        Label messaegeLabel = new Label("Message");
        TextField messageField = new TextField();
        messageField.setPromptText("Mesaj");

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(ipLabel,0,0);
        grid.add(ipField,1,0);

        grid.add(portLabel,0,1);
        grid.add(portField,1,1);

        grid.add(messaegeLabel,0,2);
        grid.add(messageField,1,2);

        HBox buttons = new HBox(20);

        Button loadButton = new Button("Load JSON");
        Button saveButton = new Button("Save JSON");
        Button sendButton = new Button("Send");

        buttons.getChildren().addAll(loadButton,saveButton,sendButton);

        root.getChildren().addAll(title,grid,buttons,status);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 300);
        // _________________________ Sahne Oluştu




        loadButton.setOnAction(e->{

            config = parser.getJSONdata();
            status.setText("Status : JSON Ayarları Yüklendi.....!");


            ipField.setText(config.getNetwork().getIp());
            portField.setText(String.valueOf(config.getNetwork().getPort()));
            messageField.setText(config.getMessage().getText());

        });

        saveButton.setOnAction(e->{

            status.setText("Status : Yeni JSON Ayarları Kaydedildi.....!");

        });

        sendButton.setOnAction(e->{

        sender.send(config);
            status.setText("Status : Paket Gönderildi.....!");


        });


















//        sendButton.setOnAction(e -> {
//
//            UdpSender sender = new UdpSender();
//            sender.send();
//
//        });

        stage.setTitle("UDP Sender");
        stage.setScene(scene);
        stage.show();



    }
}