package com.mk.trakit;

public class User {
    private String id, nameSurname, email, phoneno;

    public User() {
    }

    public User(String id, String nameSurname, String email, String phoneno) {
        this.id = id;
        this.nameSurname = nameSurname;
        this.email = email;
        this.phoneno = phoneno;
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

    public String getPhoneno() {
        return phoneno;
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

    public void setPhoneno(String phoneno) {this.phoneno = phoneno; }

}
