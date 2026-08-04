package org.example;

import java.util.ArrayList;
import java.util.List;

public class CustomMessage {

    private String name;
    private List<CustomMessageField> fields = new ArrayList<>();

    // "BIG" ya da "LITTLE" - mesaj başına tek bir format, varsayılan Big Endian (kanonik)
    private String endianness = "BIG";

    public CustomMessage(){

    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public List<CustomMessageField> getFields(){
        return fields;
    }

    public void setFields(List<CustomMessageField> fields){
        this.fields = fields;
    }

    public String getEndianness(){
        return endianness;
    }

    public void setEndianness(String endianness){
        this.endianness = endianness;
    }

}