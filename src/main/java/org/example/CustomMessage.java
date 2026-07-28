package org.example;

import java.util.ArrayList;
import java.util.List;

public class CustomMessage {

    private String name;

    private List<CustomMessageField> fields = new ArrayList<>();

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

}
