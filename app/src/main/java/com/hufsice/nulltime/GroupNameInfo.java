package com.hufsice.nulltime;

import java.util.ArrayList;

//그룹 정보 저장 클래스(그룹 이름, 멤버정보)
public class GroupNameInfo {

    private String groupName;
    private ArrayList<Member> members;

    public GroupNameInfo() {
        // 기본 생성자
    }
    public GroupNameInfo(String groupName, ArrayList<Member> members) {
        this.groupName = groupName;
        this.members = members;
    }

    public String getName() {
        return groupName;
    }
}
