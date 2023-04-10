package com.example.smarttaskmanager.Models;

import java.io.Serializable;

public class Passwords implements Serializable {
    String accid;
    String id;

    String password;

    public Passwords(String accid, String id, String password) {
        this.accid = accid;
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Passwords() {
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
