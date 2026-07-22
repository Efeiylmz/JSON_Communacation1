package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {

    private Config config;
    private TextField ipField;
    private TextField portField;
    private TextField messageField;
    private TextField intervalField;
    private CheckBox streamCheckBox;

    private void Config_Save(){

        config.getNetwork().setIp(ipField.getText());
        config.getNetwork().setPort(Integer.parseInt(portField.getText()));
        config.getMessage().setText(messageField.getText());
        config.gettimer().setIntervalMs(Integer.parseInt(intervalField.getText()));
        config.gettimer().setTimerEnabled(streamCheckBox.isSelected());

    }


    @Override
    public void start(Stage stage) {

        JSONParser parser = new JSONParser();
        UdpSender sender = new UdpSender();

        VBox root = new VBox(20);

        Label title = new Label("UDP Packet Sender");

        Label status = new Label("Status :");

        Label ipLabel = new Label("IP Label");
        ipField = new TextField();
        ipField.setPromptText("111.111.111.111");

        Label portLabel = new Label("Port Label");
        portField = new TextField();
        portField.setPromptText("12345");

        Label messaegeLabel = new Label("Message");
        messageField = new TextField();
        messageField.setPromptText("Mesaj");

        Label intervalLabel = new Label("Interval time");
        intervalField = new TextField();
        Label msindicator = new Label("ms");

        Label streamStatus = new Label("Stream : " );

        streamCheckBox = new CheckBox("Stream Enabled");


        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(ipLabel,0,0);
        grid.add(ipField,1,0);

        grid.add(portLabel,0,1);
        grid.add(portField,1,1);

        grid.add(messaegeLabel,0,2);
        grid.add(messageField,1,2);

        grid.add(intervalLabel,0,3);
        grid.add(intervalField,1,3);
        grid.add(msindicator,2,3);

        HBox buttons = new HBox(20);

        Button loadButton = new Button("Load JSON");
        Button saveButton = new Button("Save JSON");
        Button sendOnceButton = new Button("Send Once");
        Button streamButton = new Button("Stream");

        buttons.getChildren().addAll(loadButton,saveButton,sendOnceButton,streamButton);

        root.getChildren().addAll(
                grid,
                streamCheckBox,
                buttons,
                status

        );
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 500, 400);
        // _________________________ Sahne Oluştu




        loadButton.setOnAction(e->{

            config = parser.getJSONdata();

            ipField.setText(config.getNetwork().getIp());
            portField.setText(String.valueOf(config.getNetwork().getPort()));
            messageField.setText(config.getMessage().getText());
            intervalField.setText(String.valueOf(config.gettimer().getIntervalMs()));


            if (config.gettimer().isTimerEnabled()){

                streamCheckBox.setSelected(true);


            } else{

                streamButton.setDisable(true);
            }



            status.setText("Status : JSON Ayarları Yüklendi.....!");
        });

        saveButton.setOnAction(e->{

            Config_Save();
            parser.saveJSONData(config);

            status.setText("Status : Yeni JSON Ayarları Kaydedildi.....!");

        });

        sendOnceButton.setOnAction(e->{

        sender.send(config);
            status.setText("Status : Paket Gönderildi.....!");


        });

        streamButton.setOnAction(e->{


        });

        streamCheckBox.setOnAction(e->{
            if(streamCheckBox.isSelected()){
                streamButton.setDisable(false);
            } else {
                streamButton.setDisable(true);
            }


        });

        stage.setTitle("UDP Sender");
        stage.setScene(scene);
        stage.show();



    }
}