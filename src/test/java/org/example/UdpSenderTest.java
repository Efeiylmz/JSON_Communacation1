package org.example;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UdpSenderTest {

    UdpSender sender = new UdpSender();


    private Config createValidConfig(){

        Config config = new Config();

        NetworkConfig network = new NetworkConfig();
        network.setIp("127.0.0.1");
        network.setPort(5000);

        MessageConfig message = new MessageConfig();
        message.setText("AA");

        TimerConfig timer = new TimerConfig();
        timer.setIntervalMs(100);

        config.setNetwork(network);
        config.setMessage(message);
        config.setTimer(timer);



        return config;
    }


    @Test
    void shouldIgnoreSpaces() {

        String hex = "AA BB CC";

        UdpSender sender = new UdpSender();

        byte [] result = sender.hexStringToByteArray(hex);

        byte [] expected = {

                (byte)0xAA,
                (byte)0xBB,
                (byte)0xCC
        };
        assertArrayEquals(expected,result);
    }

    @Test
    void shouldHandleLowercaseHex() {
        byte[] result = sender.hexStringToByteArray("aa bb");
        byte[] expected = { (byte) 0xAA, (byte) 0xBB };
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldThrowNullPointerExceptionForNullInput() {
        assertThrows(NullPointerException.class, () -> sender.hexStringToByteArray(null));
    }

    @Test
    void shouldReturnEmptyArrayForEmptyString() {
        byte[] result = sender.hexStringToByteArray("");
        assertArrayEquals(new byte[0], result);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOddLength() {

        String hex = "AA BB C";


        assertThrows(IllegalArgumentException.class,() -> sender.hexStringToByteArray(hex));

    }

    @Test
    void shouldThrowExceptionForInvalidHex() {

        assertThrows(
                NumberFormatException.class,
                () -> sender.hexStringToByteArray("ZZ")
        );

    }

//    @Test
//    void shouldHandleInvalidConfig(){
//    Config config = createValidConfig();
//    config.getNetwork().setIp("Invalid-ip");
//    assertDoesNotThrow( ()-> sender.send(config) );
//    }

    @Test
    void sendShouldPropagateExceptionForOddLengthMessage() {
        Config config = createValidConfig();
        config.getMessage().setText("AA B"); //geçersiz
        assertThrows(IllegalArgumentException.class, () -> sender.send(config));
    }

    @Test
    void shouldSendSuccessfullyWithValidConfig() {
        Config config = createValidConfig();
        assertDoesNotThrow(() -> sender.send(config));
    }

    // ---------- validate ----------

    @Test
    void validate_shouldNotThrowForValidConfig() {
        Config config = createValidConfig();
        assertDoesNotThrow(() -> sender.validate(config));
    }

    @Test
    void validate_shouldThrowForOddLengthMessage() {
        Config config = createValidConfig();
        config.getMessage().setText("AA B"); //geçersiz
        assertThrows(IllegalArgumentException.class, () -> sender.validate(config));
    }

    @Test
    void validate_shouldThrowForInvalidHexCharacters() {
        Config config = createValidConfig();
        config.getMessage().setText("ZZ"); //geçersiz
        assertThrows(NumberFormatException.class, () -> sender.validate(config));
    }

}