package com.mk.trakit;


import com.google.android.gms.tasks.Tasks;

public class Room {

    private String room_name;
    private String[] member = new String[10];
    private String[] mid;
    private String[] tid;
    private Task[] tasks;
    private String id;

    public Room() {
    }

    public Room(String id, String room_name, String[] mid, String[] member, Task[] tasks){
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

    public String[] getMember() {
        return member;
    }

    public void setMember(String[] member) {
        this.member = member;
    }

    public String[] getMid() {
        return mid;
    }

    public void setMid(String[] mid) {
        this.mid = mid;
    }

    public String[] getTid() {
        return tid;
    }

    public void setTid(String[] tid) {
        this.tid = tid;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }
}
