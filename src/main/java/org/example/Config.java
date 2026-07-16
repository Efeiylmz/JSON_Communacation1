
package org.example;

public class Config {

    private String ip;
    private int port;
    private String message;


    public Config() {
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return message;
    }

    public void setIp(String network_ip) {
        this.ip = network_ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}