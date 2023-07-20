package com.mk.trakit;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {

    public static DatabaseReference getTasks(){
        return FirebaseDatabase.getInstance().getReference().child("Tasks");
    }

    public static DatabaseReference getTaskAssignments(){
        return FirebaseDatabase.getInstance().getReference().child("TaskAssignments");
    }

    public static DatabaseReference getRooms(){
        return FirebaseDatabase.getInstance().getReference().child("Rooms");
    }

    public static DatabaseReference getRoomMembers(){
        return FirebaseDatabase.getInstance().getReference().child("RoomMembers");
    }

    public static DatabaseReference getUsers(){
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public static DatabaseReference getUserTokens(){
        return FirebaseDatabase.getInstance().getReference().child("UserTokens");
    }

    public static DatabaseReference getMessages(){
        return FirebaseDatabase.getInstance().getReference().child("Messages");
    }

}
