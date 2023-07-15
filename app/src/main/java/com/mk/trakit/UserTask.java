package com.mk.trakit;

public class UserTask {

    private String id;

    private boolean status;

    public UserTask() {
    }

    public UserTask(String id, Boolean status){
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
