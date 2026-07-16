package org.example;

public class NetworkConfig{

    private String ip;
    private int port;

    public NetworkConfig(){}


    public String getIp(){
        return ip;
    }

    public int getPort(){
        return port;

    }

    public void setIp(String network_ip){

        this.ip = network_ip;
    }

    public void setPort(int network_port){

        this.port = network_port;
    }
}
