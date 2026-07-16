
package org.example;

public class Config {

    private NetworkConfig network;

    private MessageConfig message;

    public Config(){

    }

    public NetworkConfig getNetwork(){

        return network;
    }
    public MessageConfig getMessage(){

        return message;
    }

    public void setNetwork(NetworkConfig network){

        this.network = network;
    }

    public void setMessage(MessageConfig message){

        this.message = message;
    }



}