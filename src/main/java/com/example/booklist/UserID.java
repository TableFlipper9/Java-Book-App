package com.example.booklist;

public abstract class UserID {
    public static int userID;

    public static int getUserID(){
        return userID;
    }
    public static void setUserID(int userID){
        UserID.userID = userID;
    }
}
