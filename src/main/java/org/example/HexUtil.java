package org.example;

public final class HexUtil {

    private HexUtil() {}

    /** "8 Bit" -> 8, "16 Bit" -> 16 */
    public static int bitsOf(String bitLength) {
        if (bitLength == null) {
            throw new IllegalArgumentException("Bit uzunluğu seçilmedi.");
        }
        String digits = bitLength.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz bit uzunluğu: " + bitLength);
        }
        return Integer.parseInt(digits);
    }

    /** Kullanıcı girdisini sabit uzunlukta hex string'e çevirir. */
    public static String toHex(String bitLength, String value) {

        int bits = bitsOf(bitLength);
        int hexChars = bits / 4;          // 8 -> 2, 16 -> 4
        long max = (1L << bits) - 1;      // 8 -> 255, 16 -> 65535

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Değer boş olamaz.");
        }

        String v = value.trim();
        long parsed;

        try {
            if (v.startsWith("0x") || v.startsWith("0X")) {
                parsed = Long.parseLong(v.substring(2), 16);
            } else {
                parsed = Long.parseLong(v, 10);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "'" + v + "' sayı değil. Ondalık (255) veya hex (0xFF) girin.");
        }

        if (parsed < 0 || parsed > max) {
            throw new IllegalArgumentException(
                    "'" + v + "' " + bits + " bit'e sığmıyor (0 - " + max + ").");
        }

        return String.format("%0" + hexChars + "X", parsed);
    }

    /** Alanları sırayla birleştirip tek mesaj üretir. */
    public static String build(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        for (CustomMessageField f : message.getFields()) {
            sb.append(f.getHex());
        }
        return sb.toString();
    }
}