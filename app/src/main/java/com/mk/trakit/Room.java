package com.mk.trakit;


import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.List;

public class Room {

    private String room_name;

    private String id;

    public Room() {
    }

    public Room(String id, String room_name){
        this.id = id;
        this.room_name = room_name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

}
