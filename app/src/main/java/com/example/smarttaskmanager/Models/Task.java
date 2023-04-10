package com.example.smarttaskmanager.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Task implements Serializable {
    static final public String TODO = "To do";
    static final public String IN_PROGRESS = "In Progress";
    static final public String DONE = "Done";
    static final public String LOW = "low";
    static final public String MEDIUM = "medium";
    static final public String HIGH = "high";

    protected String taskID;
    protected String taskTitle;
    protected String taskDescription;
    protected String taskAssigned;
    protected String taskDeadline;
    protected String taskAssignedTo;
    protected String taskAssignedFrom;
    protected String taskStatus;

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    protected String taskPriority;
    protected ArrayList<Users> grpTask;
    protected boolean isSeen = false;
    protected Date taskAssignedDate;
    protected Date taskDeadlineDate;

    public Task() {
    }

    public Task(String taskID, String taskTitle, String taskDescription, String taskAssigned, String taskDeadline, String taskAssignedTo, String taskAssignedFrom, String taskStatus, ArrayList<Users> grpTask, boolean isSeen, Date taskAssignedDate, Date taskDeadlineDate) {
        this.taskID = taskID;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskAssigned = taskAssigned;
        this.taskDeadline = taskDeadline;
        this.taskAssignedTo = taskAssignedTo;
        this.taskAssignedFrom = taskAssignedFrom;
        this.taskStatus = taskStatus;
        this.grpTask = grpTask;
        this.isSeen = isSeen;
        this.taskAssignedDate = taskAssignedDate;
        this.taskDeadlineDate = taskDeadlineDate;
    }


    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskAssigned() {
        return taskAssigned;
    }

    public void setTaskAssigned(String taskAssigned) {
        this.taskAssigned = taskAssigned;
    }

    public String getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(String taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public String getTaskAssignedTo() {
        return taskAssignedTo;
    }

    public void setTaskAssignedTo(String taskAssignedTo) {
        this.taskAssignedTo = taskAssignedTo;
    }

    public String getTaskAssignedFrom() {
        return taskAssignedFrom;
    }

    public void setTaskAssignedFrom(String taskAssignedFrom) {
        this.taskAssignedFrom = taskAssignedFrom;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public ArrayList<Users> getGrpTask() {
        return grpTask;
    }

    public void setGrpTask(ArrayList<Users> grpTask) {
        this.grpTask = grpTask;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Date getTaskAssignedDate() {
        return taskAssignedDate;
    }

    public void setTaskAssignedDate(Date taskAssignedDate) {
        this.taskAssignedDate = taskAssignedDate;
    }

    public Date getTaskDeadlineDate() {
        return taskDeadlineDate;
    }

    public void setTaskDeadlineDate(Date taskDeadlineDate) {
        this.taskDeadlineDate = taskDeadlineDate;
    }
}

