package com.mk.trakit;

public class User {
    private String id, nameSurname, email, pictureUrl, type;

    public User() {
    }

    public User(String id, String nameSurname, String email, String pictureUrl, String type) {
        this.id = id;
        this.nameSurname = nameSurname;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public String getEmail() {
        return email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setType(String type) {
        this.type = type;
    }
}
