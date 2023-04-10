package com.example.smarttaskmanager.Models;

import java.io.Serializable;

public class Users implements Serializable {
    private String userName;
    private String userEmail;
    private String userPass;
    private String fireuserid;
    private String userDept;
    private String userProfile;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
    final public static String Android_dept = "Android";
    final public static String Web_Dept = "Web";
    final public static String Ui_Ux_Dept = "UI/UX";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getFireuserid() {
        return fireuserid;
    }

    public void setFireuserid(String fireuserid) {
        this.fireuserid = fireuserid;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    final public static String MBA_Dept = "MBA";
    final public static String No_Profile = "No Profile";

    public Users(String userName, String userEmail, String userPass, String fireuserid) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.fireuserid = fireuserid;
    }

    public Users() {

    }
}
