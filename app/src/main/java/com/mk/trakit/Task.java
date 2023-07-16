package com.mk.trakit;

import com.google.type.DateTime;

public class Task {

    private String id;
    private String task_name;
    private String description;
    private String room_id;
    private DateTime due_date;
    private DateTime time_created;
    private String user_id;

    public Task() {
    }

    public Task(String id, String task_name, String description, String room_id, DateTime due_date, DateTime time_created, String user_id){
        this.id = id;
        this.task_name = task_name;
        this.description = description;
        this.room_id = room_id;
        this.due_date = due_date;
        this.time_created = time_created;
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public DateTime getDue_date() {
        return due_date;
    }

    public void setDue_date(DateTime due_date) {
        this.due_date = due_date;
    }

    public DateTime getTime_created() {
        return time_created;
    }

    public void setTime_created(DateTime time_created) {
        this.time_created = time_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
