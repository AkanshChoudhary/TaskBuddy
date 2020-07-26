package com.example.taskbuddy;

public class TaskItem {
    String taskName;
    String taskDesc;
    String taskStatus;
    String dueDate;
    String assignedTo;
    public TaskItem(String taskName, String taskDesc, String taskStatus,String dueDate,String assignedTo) {
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
        this.dueDate=dueDate;
        this.assignedTo=assignedTo;
       }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
