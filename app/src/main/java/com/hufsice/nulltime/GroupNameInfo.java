package com.hufsice.nulltime;

import java.util.ArrayList;

public class GroupNameInfo {
    private String groupId;
    private String groupName;
    private ArrayList<Member> members;

    public GroupNameInfo() {
        // 기본 생성자
    }
    public GroupNameInfo(String groupName, ArrayList<Member> members) {
        this.groupName = groupName;
        this.members = members;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }
}
