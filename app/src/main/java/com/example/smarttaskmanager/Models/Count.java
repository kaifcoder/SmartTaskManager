package com.example.smarttaskmanager.Models;

import java.io.Serializable;

public class Count implements Serializable {
    int todo, inProgress, done, all;

    public int getTodo() {
        return todo;
    }

    public void setTodo(int todo) {
        this.todo = todo;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public Count() {

    }

    public Count(int todo, int inProgress, int done, int all) {
        this.todo = todo;
        this.inProgress = inProgress;
        this.done = done;
        this.all = all;
    }
}
