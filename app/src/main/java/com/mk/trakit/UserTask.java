package com.mk.trakit;

public class UserTask {

    private String uid;

    private boolean status;

    public UserTask() {
    }

    public UserTask(String uid, Boolean status){
        this.uid = uid;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
