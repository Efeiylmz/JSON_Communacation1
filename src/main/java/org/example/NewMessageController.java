package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NewMessageController {



    @FXML private VBox fieldsBox;
    @FXML private TextField newMessageName;
    @FXML private ComboBox<String> savedMessages;
    @FXML private Label preview;
    @FXML private Label status;

    private Config config;

    private final CustomMessageService messageService = new CustomMessageService();

    // ---------- kurulum ----------

    public void setConfig(Config config) {
        this.config = config;
        refreshSavedMessages();
    }

    private void refreshSavedMessages() {
        savedMessages.getItems().clear();
        if (config.getCustomMessagesList() == null) return;
        for (CustomMessage m : config.getCustomMessagesList()) {
            savedMessages.getItems().add(m.getName());
        }
    }





    // ---------- kayıtlı mesajı geri yükleme ----------

    @FXML
    private void loadSelectedMessage() {

        String name = savedMessages.getValue();
        if (name == null) return;

        CustomMessage found = messageService.findByName(config, name);
        if (found == null) return;

        newMessageName.setText(found.getName());
        fieldsBox.getChildren().clear();

        for (CustomMessageField f : found.getFields()) {
            fieldsBox.getChildren().add(createRow(f.getBitLength(), f.getValue()));
        }

        updatePreview();
        status.setText(found.getFields().size() + " field loaded.");
    }

    @FXML
    private void clearForm() {
        savedMessages.setValue(null);
        newMessageName.clear();
        fieldsBox.getChildren().clear();
        updatePreview();
        status.setText("");
    }

    @FXML
    private void deleteMessage() {

        String name = savedMessages.getValue();

        if (name == null || name.isBlank()) {
            status.setText("Select a message first");
            return;
        }

        CustomMessage found = messageService.findByName(config,name);

        if (found == null) {
            status.setText("Message not found: " + name);
            return;
        }

        messageService.delete(config, found);

        refreshSavedMessages();
        clearForm();
        status.setText(name + " deleted.");
    }

    // ---------- satır yönetimi ----------

    @FXML
    public void addNewField() {
        fieldsBox.getChildren().add(createRow(null, null));
        updatePreview();
    }

    private HBox createRow(String bitLength, String value) {

        HBox row = new HBox(15);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("8 Bits", "16 Bits");
        comboBox.setPromptText("Bit length");
        comboBox.setValue(bitLength);
        comboBox.valueProperty().addListener((o, a, b) -> updatePreview());

        TextField valueField = new TextField(value);
        valueField.setPromptText("255 or 0xFF");
        valueField.textProperty().addListener((o, a, b) -> updatePreview());

        Label hexLabel = new Label("-");
        hexLabel.setPrefWidth(60);

        Button removeButton = new Button("X");
        removeButton.setOnAction(e -> {//4Sels4n+78
            fieldsBox.getChildren().remove(row);
            updatePreview();
        });

        row.getChildren().addAll(comboBox, valueField, hexLabel, removeButton);
        return row;
    }

    // ---------- canlı önizleme ----------

    private void updatePreview() {

        StringBuilder combined = new StringBuilder();

        for (var node : fieldsBox.getChildren()) {

            if (!(node instanceof HBox row)) continue;

            ComboBox<String> comboBox = (ComboBox<String>) row.getChildren().get(0);
            TextField valueField = (TextField) row.getChildren().get(1);
            Label hexLabel = (Label) row.getChildren().get(2);

            try {
                // javanın kendi imkanlarıyla yapabiliyor muyuz
                String hex = HexUtil.toHex(comboBox.getValue(), valueField.getText());
                hexLabel.setText("→ " + hex);
                combined.append(hex);
            } catch (IllegalArgumentException e) {
                hexLabel.setText("→ ?");
            }
        }

        preview.setText(combined.length() == 0 ? "-" : combined.toString());
    }

    // ---------- kaydetme ----------

    @FXML
    public void saveMessages() {

        String name = newMessageName.getText();

        if (name == null || name.isBlank()) {
            status.setText("Failed to save : Message name cant be empty.");
            return;
        }

        CustomMessage message = new CustomMessage();
        message.setName(name.trim());

        int rowNo = 0;  // magic number

        for (var node : fieldsBox.getChildren()) {
            if (!(node instanceof HBox row)) continue;
            rowNo++;

            ComboBox<String> comboBox = (ComboBox<String>) row.getChildren().get(0);
            TextField valueField = (TextField) row.getChildren().get(1);

            try {
                String hex = HexUtil.toHex(comboBox.getValue(), valueField.getText());
                message.getFields().add(new CustomMessageField(
                        comboBox.getValue(),
                        valueField.getText().trim(),
                        hex));

            } catch (IllegalArgumentException e) {
                status.setText(rowNo + ". row: " + e.getMessage());
                return;   // hiçbir şey kaydetme
            }
        }

        if (message.getFields().isEmpty()) {
            status.setText("Add at least 1 field.");
            return;
        }

        String combinedHex = messageService.Update(config, message);

        refreshSavedMessages();
        savedMessages.setValue(message.getName());
        status.setText("Saved to JSON -> " + combinedHex);

    }
}