package com.hufsice.nulltime;

public class Member {
    private String name;
    private String id;
    private boolean isSelected;

    public Member() {
        // 인자 없는 생성자
    }

    public Member(String name, String id) {
        this.name = name;
        this.id = id;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
