package org.example;

public final class HexUtil {

    private HexUtil() {}


    public static int bitsOf(String bitLength) {
        if (bitLength == null) {
            throw new IllegalArgumentException("Bit lenght isnt selected.");
        }
        String digits = bitLength.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Invalid bit lenght: " + bitLength);
        }
        return Integer.parseInt(digits);
    }

    public static String toHex(String bitLength, String value) {

        int bits = bitsOf(bitLength);
        int hexChars = (bits + 3) / 4;
        long max = (1L << bits) - 1;

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cant be empty.");
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
                    "'" + v + "' isnt integer. Enter Decimal (255) or hex (0xFF).");
        }

        if (parsed < 0 || parsed > max) {
            throw new IllegalArgumentException(
                    "'" + v + "' " + bits + " bit overflow (0 - " + max + ").");
        }

        return String.format("%0" + hexChars + "X", parsed);
    }
    public static String build(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        for (CustomMessageField f : message.getFields()) {
            sb.append(f.getHex());
        }

        String combined = sb.toString();

        // Little Endian seçiliyse mesajın TÜM bitleri (byte sınırı gözetmeden) ters çevrilir.
        if ("LITTLE".equals(message.getEndianness())) {
            combined = reverseBitOrder(combined);
        }

        return combined;
    }

    /**
     * Verilen hex string'in bit sırasını tek tek (byte hizasına bakmadan) ters çevirir.
     * Örnek: "2D" -> ikili: 0010 1101 -> ters: 1011 0100 -> "B4"
     * Nibble (hex karakter) sayısı değişmez, sadece bitlerin sırası tersine döner.
     */

    public static String reverseBitOrder(String hex) {
        if (hex == null || hex.isEmpty()) {
            return hex;
        }

        //  binary string oluşumu

        StringBuilder binary = new StringBuilder(hex.length() * 4);
        for (char c : hex.toCharArray()) {
            int nibble = Character.digit(c, 16);
            if (nibble < 0) {
                throw new IllegalArgumentException("Invalid hex digit: " + c);
            }
            binary.append(String.format("%4s", Integer.toBinaryString(nibble)).replace(' ', '0'));
        }
        // binary string ters dönmesi
        binary.reverse();

        StringBuilder result = new StringBuilder(hex.length());

        // string integer dönüşümü

        for (int i = 0; i < binary.length(); i += 4) {
            int nibbleValue = Integer.parseInt(binary.substring(i, i + 4), 2);
            result.append(Integer.toHexString(nibbleValue).toUpperCase());
        }

        return result.toString();
    }
}