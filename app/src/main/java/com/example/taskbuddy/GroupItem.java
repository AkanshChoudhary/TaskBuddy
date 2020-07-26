package com.example.taskbuddy;

public class GroupItem {
    String groupName;
    String groupCode;

    public GroupItem(String groupName, String groupCode) {
        this.groupName = groupName;
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
