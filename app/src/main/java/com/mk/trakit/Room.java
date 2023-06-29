package com.mk.trakit;


import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.List;

public class Room {

    private String room_name;
    private HashMap<String, User> member;
    private HashMap<String, String> mid;
    private HashMap<String, String> tid;
    private HashMap<String, Task> tasks;
    private String id;

    public Room() {
    }

    public Room(String id, String room_name, HashMap<String, String> mid, HashMap<String, User> member, HashMap<String, Task> tasks){
        this.id = id;
        this.room_name = room_name;
        this.mid = mid;
        this.member = member;
        this.tasks = tasks;
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

    public HashMap<String, User> getMember() {

        return member;
    }

    public void setMember(HashMap<String, User> member) {
        this.member = member;
    }

    public HashMap<String, String> getMid() {
        return mid;
    }

    public void setMid(HashMap<String, String> mid) {
        this.mid = mid;
    }

    public HashMap<String, String> getTid() {
        return tid;
    }

    public void setTid(HashMap<String, String> tid) {
        this.tid = tid;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, Task> tasks) {
        this.tasks = tasks;
    }
}
