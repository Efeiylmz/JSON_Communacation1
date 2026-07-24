package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;

public class newMessageController {

    @FXML
    private VBox fieldsBox;

    @FXML
    private TextField newMessageName;


    @FXML
    public void addNewField(){

        HBox newMessageBoxes= new HBox(30);
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                null,
                "8Byte",
                "16Byte",
                "32Byte"
        );

        TextField textBox = new TextField();
        textBox.setPromptText("Enter Message");

        newMessageBoxes.getChildren().addAll(comboBox,textBox);

        fieldsBox.getChildren().add(newMessageBoxes);
    }

    public void saveMessages(){

        System.out.println("Kaydedildi");
    }

}


