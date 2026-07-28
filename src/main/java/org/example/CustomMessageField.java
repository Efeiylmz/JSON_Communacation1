package org.example;

public class CustomMessageField {

    private String bitLength;
    private String value;   // kullanıcının girdiği ham değer: "255"
    private String hex;     // hesaplanan çıktı: "FF"

    public CustomMessageField() {}

    public CustomMessageField(String bitLength, String value, String hex) {
        this.bitLength = bitLength;
        this.value = value;
        this.hex = hex;
    }

    public String getBitLength() { return bitLength; }
    public void setBitLength(String bitLength) { this.bitLength = bitLength; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }
}