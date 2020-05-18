package com.example.flipr;

import java.util.ArrayList;

public class TeamBoardClass {
    String Name;
    String adminId;
    ArrayList<TeamMemberClass> members;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public ArrayList<TeamMemberClass> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<TeamMemberClass> members) {
        this.members = members;
    }
}
