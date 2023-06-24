package com.mk.trakit;

public class UserTask {

    private String uid;
    private String tid;
    private boolean status;

    public UserTask() {
    }

    public UserTask(String uid, String tid, Boolean status){
        this.uid = uid;
        this.tid = tid;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
