package org.example;

public class TimerConfig {

    private int intervalMs;

    private boolean timerEnabled;

    public int getIntervalMs(){

        return intervalMs;
    }

    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public boolean isTimerEnabled(){
        return timerEnabled;
    }

    public void setTimerEnabled(boolean timerEnabled){

        this.timerEnabled = timerEnabled;
    }
}
