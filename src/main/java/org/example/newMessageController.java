package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class newMessageController {

    private final JSONParser parser = new JSONParser();

    @FXML private VBox fieldsBox;
    @FXML private TextField newMessageName;
    @FXML private ComboBox<String> savedMessages;
    @FXML private Label preview;
    @FXML private Label status;

    private Config config;

    // ---------- kurulum ----------

    public void setConfig(Config config) {
        this.config = config;
        refreshSavedMessages();
    }

    private void refreshSavedMessages() {
        savedMessages.getItems().clear();
        if (config.getCustomMessages() == null) return;
        for (CustomMessage m : config.getCustomMessages()) {
            savedMessages.getItems().add(m.getName());
        }
    }

    private CustomMessage findByName(String name) {
        if (config.getCustomMessages() == null) return null;
        for (CustomMessage m : config.getCustomMessages()) {
            if (name.equals(m.getName())) return m;
        }
        return null;
    }

    // ---------- kayıtlı mesajı geri yükleme ----------

    @FXML
    private void loadSelectedMessage() {

        String name = savedMessages.getValue();
        if (name == null) return;

        CustomMessage found = findByName(name);
        if (found == null) return;

        newMessageName.setText(found.getName());
        fieldsBox.getChildren().clear();

        for (CustomMessageField f : found.getFields()) {
            fieldsBox.getChildren().add(createRow(f.getBitLength(), f.getValue()));
        }

        updatePreview();
        status.setText(found.getFields().size() + " alan yüklendi.");
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
            status.setText("Önce silinecek mesajı seçin.");
            return;
        }

        CustomMessage found = findByName(name);

        if (found == null) {
            status.setText("Mesaj bulunamadı: " + name);
            return;
        }

        config.getCustomMessages().remove(found);   // 1) bellekten çıkar
        parser.saveJSONData(config);                // 2) diske yaz

        refreshSavedMessages();                     // 3) ComboBox'ı yenile
        clearForm();                                // 4) formu boşalt
        status.setText(name + " silindi.");         // 5) en son mesajı yaz
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
        comboBox.getItems().addAll("8 Bit", "16 Bit");
        comboBox.setPromptText("Bit length");
        comboBox.setValue(bitLength);
        comboBox.valueProperty().addListener((o, a, b) -> updatePreview());

        TextField valueField = new TextField(value);
        valueField.setPromptText("255 veya 0xFF");
        valueField.textProperty().addListener((o, a, b) -> updatePreview());

        Label hexLabel = new Label("-");
        hexLabel.setPrefWidth(60);

        Button removeButton = new Button("X");
        removeButton.setOnAction(e -> {
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
            status.setText("Kaydedilmedi : Mesaj adı boş olamaz.");
            return;
        }

        CustomMessage message = new CustomMessage();
        message.setName(name.trim());

        int rowNo = 0;

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
                status.setText(rowNo + ". satır: " + e.getMessage());
                return;   // hiçbir şey kaydetme
            }
        }

        if (message.getFields().isEmpty()) {
            status.setText("En az bir alan ekleyin.");
            return;
        }

        if (config.getCustomMessages() == null) {
            config.setCustomMessages(new ArrayList<>());
        }

        CustomMessage existing = findByName(message.getName());

        if (existing != null) {
            int index = config.getCustomMessages().indexOf(existing);
            config.getCustomMessages().set(index, message);
        } else {
            config.getCustomMessages().add(message);
        }

        // --- YENİ: hex çıktısını aktif mesaj olarak ayarla ---
        String combinedHex = HexUtil.build(message);

        if (config.getMessage() == null) {
            config.setMessage(new MessageConfig());
        }

        String previousText = config.getMessage().getText();   // rollback için sakla
        config.getMessage().setText(combinedHex);
        // ------------------------------------------------------

//        if (parser.saveJSONData(config)) {
//            config.getMessage().setText(previousText);         // geri al
//            status.setText("Kaydedilemedi: dosyaya yazılamadı.");
//            return;
//        }

        refreshSavedMessages();
        savedMessages.setValue(message.getName());
        status.setText("Kaydedildi ve aktif edildi → " + combinedHex);
        config.getMessage().setText(combinedHex);
        parser.saveJSONData(config);

    }
}