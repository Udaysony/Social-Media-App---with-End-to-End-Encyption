package com.example.socialapp.models;

public class GroupDetails {
    public String username;
    public int groupid;
    public String groupname;
    public String isOwner;
    public String isFriend;

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getisOwner() {
        return isOwner;
    }

    public void setisOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    public int getgroupid() {
        return groupid;
    }

    public void setgroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getgroupname() {
        return groupname;
    }

    public void setgroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }
}