package com.example.rashmitiwari.we_sync.model;

/**
 * Created by Rashmi on 7/10/2017.
 */

public class ReadyState {
     String ready;
     int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ReadyState() {
    }

    public ReadyState(String ready) {

        this.ready = ready;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

}
