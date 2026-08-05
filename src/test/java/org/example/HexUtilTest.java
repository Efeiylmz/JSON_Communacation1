package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HexUtilTest {

    // ---------- toHex: doğru dönüşümler ----------

    @Test
    void toHex_shouldConvert8BitDecimalValue() {
        assertEquals("0D", HexUtil.toHex("8 Bits", "13"));
        assertEquals("2D", HexUtil.toHex("8 Bits", "45"));
        assertEquals("FB", HexUtil.toHex("8 Bits", "251"));
    }

    @Test
    void toHex_shouldConvert16BitDecimalValue() {
        assertEquals("0064", HexUtil.toHex("16 Bits", "100"));
        assertEquals("FFFF", HexUtil.toHex("16 Bits", "65535"));
    }

    @Test
    void toHex_shouldAcceptHexPrefixedInput() {
        assertEquals("FB", HexUtil.toHex("8 Bits", "0xFB"));
        assertEquals("FB", HexUtil.toHex("8 Bits", "0XFB"));
    }

    @Test
    void toHex_shouldTrimWhitespaceAroundValue() {
        assertEquals("0D", HexUtil.toHex("8 Bits", "  13  "));
    }

    @Test
    void toHex_shouldAcceptMinAndMaxBoundaryValues() {
        assertEquals("00", HexUtil.toHex("8 Bits", "0"));
        assertEquals("FF", HexUtil.toHex("8 Bits", "255"));
    }

    @Test
    void toHex_shouldMatchFieldStoredInConfig() {

        Config config = createValidConfig();
        CustomMessageField field = config.getCustomMessagesList().get(0).getFields().get(0);

        String hex = HexUtil.toHex(field.getBitLength(), field.getValue());

        assertEquals("32", hex);
    }

    // ---------- toHex: hata durumları ----------

    @Test
    void toHex_shouldThrowWhenValueExceedsBitRange() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("8 Bits", "256"));
    }

    @Test
    void toHex_shouldThrowWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("8 Bits", "-1"));
    }

    @Test
    void toHex_shouldThrowWhenValueIsNotNumeric() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("8 Bits", "abc"));
    }

    @Test
    void toHex_shouldThrowWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("8 Bits", "   "));
    }

    @Test
    void toHex_shouldThrowWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("8 Bits", null));
    }

    @Test
    void toHex_shouldThrowWhenBitLengthIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex(null, "13"));
    }

    @Test
    void toHex_shouldThrowWhenBitLengthHasNoDigits() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("Bits", "13"));
    }

    // ---------- toHex: farklı bit uzunlukları (1, 4, 10, 12, 24, 32) ----------

    @Test
    void toHex_shouldConvert1BitValue() {
        assertEquals("0", HexUtil.toHex("1 Bits", "0"));
        assertEquals("1", HexUtil.toHex("1 Bits", "1"));
    }

    @Test
    void toHex_shouldThrowWhen1BitValueOverflows() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("1 Bits", "2"));
    }

    @Test
    void toHex_shouldConvert4BitValue() {
        assertEquals("0", HexUtil.toHex("4 Bits", "0"));
        assertEquals("A", HexUtil.toHex("4 Bits", "10"));
        assertEquals("F", HexUtil.toHex("4 Bits", "15"));
    }

    @Test
    void toHex_shouldThrowWhen4BitValueOverflows() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("4 Bits", "16"));
    }

    @Test
    void toHex_shouldConvert4BitDecimalValue() {
        assertEquals("3", HexUtil.toHex("4 Bits", "3"));
        assertEquals("7", HexUtil.toHex("4 Bits", "7"));
        assertEquals("C", HexUtil.toHex("4 Bits", "12"));
    }

    @Test
    void toHex_shouldConvert10BitValue() {
        // 10 bit -> hexChars = (10+3)/4 = 3, max = 1023
        assertEquals("000", HexUtil.toHex("10 Bits", "0"));
        assertEquals("0AA", HexUtil.toHex("10 Bits", "170"));
        assertEquals("3FF", HexUtil.toHex("10 Bits", "1023"));
    }

    @Test
    void toHex_shouldThrowWhen10BitValueOverflows() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("10 Bits", "1024"));
    }

    @Test
    void toHex_shouldConvert10BitDecimalValue() {
        assertEquals("005", HexUtil.toHex("10 Bits", "5"));
        assertEquals("100", HexUtil.toHex("10 Bits", "256"));
        assertEquals("2BC", HexUtil.toHex("10 Bits", "700"));
    }

    @Test
    void toHex_shouldConvert12BitValue() {
        // 12 bit -> hexChars = 3, max = 4095
        assertEquals("000", HexUtil.toHex("12 Bits", "0"));
        assertEquals("FFF", HexUtil.toHex("12 Bits", "4095"));
    }

    @Test
    void toHex_shouldConvert12BitDecimalValue() {
        assertEquals("012", HexUtil.toHex("12 Bits", "18"));
        assertEquals("123", HexUtil.toHex("12 Bits", "291"));
        assertEquals("FA0", HexUtil.toHex("12 Bits", "4000"));
    }

    @Test
    void toHex_shouldConvert24BitValue() {
        // 24 bit -> hexChars = 6, max = 16777215
        assertEquals("000001", HexUtil.toHex("24 Bits", "1"));
        assertEquals("FFFFFF", HexUtil.toHex("24 Bits", "16777215"));
    }

    @Test
    void toHex_shouldConvert24BitDecimalValue() {
        assertEquals("0000FF", HexUtil.toHex("24 Bits", "255"));
        assertEquals("010000", HexUtil.toHex("24 Bits", "65536"));
        assertEquals("0F4240", HexUtil.toHex("24 Bits", "1000000"));
    }

    @Test
    void toHex_shouldConvert32BitValue() {
        // 32 bit -> hexChars = 8, max = 4294967295
        assertEquals("00000000", HexUtil.toHex("32 Bits", "0"));
        assertEquals("FFFFFFFF", HexUtil.toHex("32 Bits", "4294967295"));
    }

    @Test
    void toHex_shouldThrowWhen32BitValueOverflows() {
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.toHex("32 Bits", "4294967296"));
    }

    @Test
    void toHex_shouldConvert32BitDecimalValue() {
        assertEquals("00000020", HexUtil.toHex("32 Bits", "32"));
        assertEquals("00001000", HexUtil.toHex("32 Bits", "4096"));
        assertEquals("000F4240", HexUtil.toHex("32 Bits", "1000000"));
        assertEquals("12345678", HexUtil.toHex("32 Bits", "305419896"));
    }

    @Test
    void toHex_shouldAcceptLowercaseHexPrefixedDigits() {
        assertEquals("FB", HexUtil.toHex("8 Bits", "0xfb"));
    }

    @Test
    void toHex_shouldPadLeadingZerosForSmallValueInLargerBitLength() {
        assertEquals("0005", HexUtil.toHex("16 Bits", "5"));
    }

    @Test
    void toHex_shouldWorkRegardlessOfBitLengthTextFormat() {
        // bitsOf sadece rakamları ayıklıyor, "8 Bits" ile "8bit" aynı sonucu vermeli
        assertEquals("0D", HexUtil.toHex("8bit", "13"));
    }

    // ---------- yardımcı metodlar ----------


    private CustomMessage createSampleMessage() {
        CustomMessage message = new CustomMessage();
        message.setName("test");
        message.setFields(List.of(
                new CustomMessageField("8 Bits", "13", "0D"),
                new CustomMessageField("8 Bits", "45", "2D"),
                new CustomMessageField("8 Bits", "251", "FB")
        ));
        return message;
    }
    private Config createValidConfig() {

        Config config = new Config();

        NetworkConfig network = new NetworkConfig();
        network.setIp("127.0.0.1");
        network.setPort(5000);

        TimerConfig timer = new TimerConfig();
        timer.setIntervalMs(100);

        CustomMessageField field = new CustomMessageField();
        field.setValue("50");
        field.setBitLength("8 Bits");
        field.setHex("32");

        CustomMessage customMessage = new CustomMessage();
        customMessage.setName("test");
        customMessage.setFields(List.of(field));

        List<CustomMessage> customMessagesList = new ArrayList<>();
        customMessagesList.add(customMessage);

        MessageConfig message = new MessageConfig();
        message.setText("32");

        config.setNetwork(network);
        config.setMessage(message);
        config.setTimer(timer);
        config.setCustomMessagesList(customMessagesList);

        return config;
    }

    // ---------- build: sabit mesajın doğru birleşmesi ----------

    @Test
    void build_shouldCombineFieldsInOrder() {

        CustomMessage message = createSampleMessage();

        String combined = HexUtil.build(message);

        assertEquals("0D2DFB", combined);

    }

    @Test
    void build_shouldReturnEmptyStringWhenNoFields() {
        CustomMessage message = new CustomMessage();
        message.setName("empty");
        message.setFields(List.of());

        assertEquals("", HexUtil.build(message));
    }

    // ---------- reverseBitOrder ----------

    @Test
    void reverseBitOrder_shouldReverseBitsWithinAndAcrossNibbles() {
        // 2D = 0010 1101 -> ters = 1011 0100 = B4
        assertEquals("B4", HexUtil.reverseBitOrder("2D"));
        // F0 = 1111 0000 -> ters = 0000 1111 = 0F
        assertEquals("0F", HexUtil.reverseBitOrder("F0"));
        // 5 (tek nibble) = 0101 -> ters = 1010 = A
        assertEquals("A", HexUtil.reverseBitOrder("5"));
    }

    @Test
    void reverseBitOrder_appliedTwice_shouldReturnOriginal() {
        String original = "52DFB";
        assertEquals(original, HexUtil.reverseBitOrder(HexUtil.reverseBitOrder(original)));
    }

    @Test
    void reverseBitOrder_shouldReturnUnchangedForNullOrEmpty() {
        assertNull(HexUtil.reverseBitOrder(null));
        assertEquals("", HexUtil.reverseBitOrder(""));
    }

    @Test
    void reverseBitOrder_shouldHandleOddNibbleCount() {
        // ABC = 1010 1011 1100 (12 bit, byte sınırında değil) -> ters = 0011 1101 0101 = 3D5
        assertEquals("3D5", HexUtil.reverseBitOrder("ABC"));
    }

    @Test
    void reverseBitOrder_shouldAcceptLowercaseHexInput() {
        assertEquals("B4", HexUtil.reverseBitOrder("2d"));
    }

    @Test
    void reverseBitOrder_shouldThrowForInvalidHexDigit() {
        assertThrows(IllegalArgumentException.class, () -> HexUtil.reverseBitOrder("2G"));
    }

    @Test
    void build_shouldReturnCanonicalHexWhenBigEndian() {
        CustomMessage message = createSampleMessage();
        message.setEndianness("BIG");
        assertEquals("0D2DFB", HexUtil.build(message));
    }

    @Test
    void build_shouldReverseBitsWhenLittleEndian() {
        CustomMessage message = createSampleMessage();
        message.setEndianness("LITTLE");
        assertEquals(HexUtil.reverseBitOrder("0D2DFB"), HexUtil.build(message));
    }

    // ---------- bitsOf ----------

    @Test
    void bitsOf_shouldExtractDigitsFromBitLength() {
        assertEquals(8, HexUtil.bitsOf("8 Bits"));
        assertEquals(16, HexUtil.bitsOf("16 Bits"));
    }

    @Test
    void bitsOf_shouldThrowWhenBitLengthIsNull() {
        assertThrows(IllegalArgumentException.class, () -> HexUtil.bitsOf(null));
    }
}