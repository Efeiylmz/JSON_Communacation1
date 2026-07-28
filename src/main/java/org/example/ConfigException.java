package org.example;

import com.sun.jdi.ThreadReference;

public class ConfigException  extends Exception{

    public ConfigException(String message){

        super(message);
    }

    public ConfigException(String message, Throwable cause){

        super(message,cause);
    }
}

