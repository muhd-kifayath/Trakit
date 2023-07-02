package com.mk.trakit;


import java.util.HashMap;
import java.util.Map;

public class User {
    private String id, name, email, phoneno, profile_pic;
    private Map<String, Boolean> rooms;

    public User() {
    }

    public User(String id, String name, String email, String phoneno, String profile_pic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneno = phoneno;
        this.profile_pic = profile_pic;
        this.rooms = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public Map<String, Boolean> getRooms() {
        return rooms;
    }

    public void addRoom(String roomId) {
        rooms.put(roomId, true);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }
}
