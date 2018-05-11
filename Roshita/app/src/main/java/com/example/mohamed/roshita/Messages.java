package com.example.mohamed.roshita;

public class Messages {
    private String message ,type ,from;

    public Messages(){}

    public Messages(String message ,String type ,String from){
        this.message = message;
        this.type = type;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}