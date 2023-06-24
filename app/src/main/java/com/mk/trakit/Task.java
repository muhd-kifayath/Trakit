package com.mk.trakit;

import com.google.type.DateTime;

public class Task {

    private String id;
    private String task_name;
    private DateTime due_date;
    private DateTime time_created;

    public Task() {
    }

    public Task(String id, String task_name, DateTime due_date, DateTime time_created){
        this.id = id;
        this.task_name = task_name;
        this.due_date = due_date;
        this.time_created = time_created;
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
}
