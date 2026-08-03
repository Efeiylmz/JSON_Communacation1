
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private NetworkConfig network;

    private MessageConfig message;

    private TimerConfig timer;

    private List<CustomMessage> customMessagesList = new ArrayList<>();

    public Config(){

    }

    public List<CustomMessage> getCustomMessagesList(){

        return customMessagesList;
    }

    public void setCustomMessagesList(List<CustomMessage> customMessagesList){

        this.customMessagesList = customMessagesList;
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

    public void setTimer(TimerConfig timer){

        this.timer = timer;
    }

    public TimerConfig getTimer(){

        return timer;
    }



}