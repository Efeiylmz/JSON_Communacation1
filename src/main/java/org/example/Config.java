
package org.example;

public class Config {

    private NetworkConfig network;

    private MessageConfig message;

    private TimerConfig timer;

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

    public void settimer(TimerConfig timer){

        this.timer = timer;
    }

    public TimerConfig getTimer(){

        return timer;
    }



}