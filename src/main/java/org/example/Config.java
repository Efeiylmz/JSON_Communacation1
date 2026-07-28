
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private NetworkConfig network;

    private MessageConfig message;

    private TimerConfig timer;

    private List<CustomMessage> customMessages = new ArrayList<>();

    public Config(){

    }

    public List<CustomMessage> getCustomMessages(){

        return customMessages;
    }

    public void setCustomMessages(List<CustomMessage> customMessages){

        this.customMessages = customMessages;
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