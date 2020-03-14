package com.example.regular;

public class Events {
    private String startEventTime,endEventTime;

    public Events(){

    }
    public Events(String startEventTime, String endEventTime) {
        this.startEventTime = startEventTime;
        this.endEventTime = endEventTime;
    }

    public String getStartEventTime() {
        return startEventTime;
    }

    public void setStartEventTime(String startEventTime) {
        this.startEventTime = startEventTime;
    }

    public String getEndEventTime() {
        return endEventTime;
    }

    public void setEndEventTime(String endEventTime) {
        this.endEventTime = endEventTime;
    }
}
