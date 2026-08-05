package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class NewMessageController {



    @FXML private VBox fieldsBox;
    @FXML private TextField newMessageName;
    @FXML private ComboBox<String> savedMessages;
    @FXML private ComboBox<String> endianness;
    @FXML private Label preview;
    @FXML private Label status;
    @FXML private HBox bitBar;

    private Config config;

    private final CustomMessageService messageService = new CustomMessageService();
    private final List<Region> bitCells = new ArrayList<>();

    private static final String BIG_LABEL = "Big Endian";
    private static final String LITTLE_LABEL = "Little Endian";

    // ---------- kurulum ----------

    @FXML
    private void initialize() {
        endianness.getItems().addAll(BIG_LABEL, LITTLE_LABEL);
        endianness.setValue(BIG_LABEL);
        buildBitBar();
    }

    // 32 bitlik doluluk barını bir kere oluşturur: 8'erli 4 grup, her biri tek bir bit kutucuğu.
    // Gruplar arası boşluk büyük (byte sınırını gösterir), grup içi boşluk küçük.
    private void buildBitBar() {
        bitBar.getChildren().clear();
        bitCells.clear();

        for (int group = 0; group < 4; group++) {
            HBox groupBox = new HBox(3);
            for (int i = 0; i < 8; i++) {
                Region cell = new Region();
                cell.getStyleClass().addAll("bit-cell", "bit-cell-empty");
                bitCells.add(cell);
                groupBox.getChildren().add(cell);
            }
            bitBar.getChildren().add(groupBox);
        }

        updateBitBar();
    }

    // fieldsBox'taki satırların toplam bit uzunluğuna göre 32 hücreyi yeniden renklendirir:
    // doldurulan bitler yeşil, mevcut byte'ı tamamlamak için gereken bitler kırmızı
    // (kullanıcı tek sayıda hex/bit bıraktığında UDP gönderiminde hata çıkmasın diye uyarı),
    // geri kalanı (henüz sırası gelmemiş) gri.
    private void updateBitBar() {
        int filled = 0;

        for (var node : fieldsBox.getChildren()) {
            if (!(node instanceof HBox row)) continue;

            ComboBox<String> comboBox = (ComboBox<String>) row.getChildren().get(0);
            TextField textField = (TextField) row.getChildren().get(1);
            String bitLength = comboBox.getValue();
            if (bitLength == null || textField.getText().isBlank()) continue;

            try {
                filled += HexUtil.bitsOf(bitLength);
            } catch (IllegalArgumentException ignored) {
                // geçersiz seçim: bit sayısına katma
            }
        }

        int cappedFilled = Math.min(filled, bitCells.size());
        int nextByteBoundary = Math.min(((cappedFilled + 7) / 8) * 8, bitCells.size());

        for (int i = 0; i < bitCells.size(); i++) {
            Region cell = bitCells.get(i);
            cell.getStyleClass().removeAll("bit-cell-filled", "bit-cell-needed", "bit-cell-empty");

            if (i < cappedFilled) {
                cell.getStyleClass().add("bit-cell-filled");
            } else if (i < nextByteBoundary) {
                cell.getStyleClass().add("bit-cell-needed");
            } else {
                cell.getStyleClass().add("bit-cell-empty");
            }
        }
    }

    // ComboBox'taki seçimi Config/CustomMessage'ın anladığı koda çevirir ("BIG"/"LITTLE")
    private String currentEndianCode() {
        return LITTLE_LABEL.equals(endianness.getValue()) ? "LITTLE" : "BIG";
    }

    // status etiketini metinle birlikte başarı(yeşil)/hata(kırmızı) rengine boyar
    private void setStatus(String message, boolean isError) {
        status.setText(message);
        status.getStyleClass().removeAll("status-success", "status-error");
        if (message != null && !message.isBlank()) {
            status.getStyleClass().add(isError ? "status-error" : "status-success");
        }
    }

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

        endianness.setValue("LITTLE".equals(found.getEndianness()) ? LITTLE_LABEL : BIG_LABEL);

        updatePreview();
        setStatus(found.getFields().size() + " field loaded. Format: " + endianness.getValue(), false);
    }

    @FXML
    private void clearForm() {
        savedMessages.setValue(null);
        newMessageName.clear();
        fieldsBox.getChildren().clear();
        endianness.setValue(BIG_LABEL);
        updatePreview();
        setStatus("", false);
    }

    @FXML
    private void deleteMessage() {

        String name = savedMessages.getValue();

        if (name == null || name.isBlank()) {
            setStatus("Select a message first", true);
            return;
        }

        CustomMessage found = messageService.findByName(config,name);

        if (found == null) {
            setStatus("Message not found: " + name, true);
            return;
        }

        messageService.delete(config, found);

        refreshSavedMessages();
        clearForm();
        setStatus(name + " deleted.", false);
    }

    // ---------- satır yönetimi ----------

    @FXML
    public void addNewField() {
        fieldsBox.getChildren().add(createRow(null, null));
        updatePreview();
    }

    private HBox createRow(String bitLength, String value) {

        HBox row = new HBox(10);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(150);
        comboBox.setMinWidth(150);
        comboBox.setMaxWidth(150);
        for (int i = 1; i <= 32; i++) {
            comboBox.getItems().add(i + " Bits");
        }
        comboBox.setPromptText("Bit length");
        comboBox.setValue(bitLength);
        comboBox.valueProperty().addListener((o, a, b) -> updatePreview());

        TextField valueField = new TextField(value);
        valueField.setPromptText("255 or 0xFF");
        valueField.textProperty().addListener((o, a, b) -> updatePreview());

        Label hexLabel = new Label("-");
        hexLabel.setPrefWidth(90);

        Button removeButton = new Button("X");
        removeButton.getStyleClass().add("button-remove");
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
                String hex = HexUtil.toHex(comboBox.getValue(), valueField.getText());
                hexLabel.setText("→ " + hex);
                combined.append(hex);
            } catch (IllegalArgumentException e) {
                hexLabel.setText("→ ?");
            }
        }

        preview.setText(combined.length() == 0 ? "-" : combined.toString());
        updateBitBar();
    }

    // ---------- kaydetme ----------

    @FXML
    public void saveMessages() {

        String name = newMessageName.getText();

        if (name == null || name.isBlank()) {
            setStatus("Failed to save : Message name cant be empty.", true);
            return;
        }

        CustomMessage message = new CustomMessage();
        message.setName(name.trim());
        message.setEndianness(currentEndianCode());

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
                setStatus(rowNo + ". row: " + e.getMessage(), true);
                return;   // hiçbir şey kaydetme
            }
        }

        if (message.getFields().isEmpty()) {
            setStatus("Add at least 1 field.", true);
            return;
        }

        String combinedHex = messageService.Update(config, message);

        refreshSavedMessages();
        savedMessages.setValue(message.getName());
        setStatus("Saved to JSON -> " + combinedHex + " (" + endianness.getValue() + ")", false);

    }

    // Not: endianness ComboBox'ındaki seçim önizlemeyi (satırlar ve "Oluşan mesaj") etkilemez;
    // önizleme her zaman kanonik/Big Endian gösterilir. Seçim yalnızca kaydedilirken
    // HexUtil.build() üzerinden nihai mesaja (config.json -> message.text, yani UDP
    // paketiyle gönderilecek veri) uygulanır.
}