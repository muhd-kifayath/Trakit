package com.mk.trakit;


import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.List;

public class Room {

    private String room_name;
    private HashMap<Integer, User> member;
    private HashMap<Integer, String> mid;
    private HashMap<Integer, String> tid;
    private HashMap<String, Task> tasks;
    private String id;

    public Room() {
    }

    public Room(String id, String room_name, HashMap<Integer, String> mid, HashMap<Integer, User> member, HashMap<String, Task> tasks){
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

    public HashMap<Integer, User> getMember() {
        return member;
    }

    public void setMember(HashMap<Integer, User> member) {
        this.member = member;
    }

    public HashMap<Integer, String> getMid() {
        return mid;
    }

    public void setMid(HashMap<Integer, String> mid) {
        this.mid = mid;
    }

    public HashMap<Integer, String> getTid() {
        return tid;
    }

    public void setTid(HashMap<Integer, String> tid) {
        this.tid = tid;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, Task> tasks) {
        this.tasks = tasks;
    }
}
