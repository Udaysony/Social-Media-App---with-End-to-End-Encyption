package com.example.socialapp.models;

import java.sql.Timestamp;

public class PostDetails {

    public int postid;
    public String post;
    public String sessionKey;
    public String digitalSignature;
    public int groupid;
    public int version_num;
    public String privacy;
    public String ownerusername;
    public int originalpostid;
    public Timestamp timestamp;

    public int getPostId() {
        return postid;
    }

    public void setPostid(int postid) {
        this.postid = postid;
    }


    public int getOriginalpostid() {
        return originalpostid;
    }

    public void setOriginalpostid(int originalpostid) {
        this.originalpostid = originalpostid;
    }


    public String getPostText() {
        return post;
    }

    public void setPostText(String post) {
        this.post = post;
    }


    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }


    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }


    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }


    public int getVersion_num() {
        return version_num;
    }

    public void setVersion_num(int version_num) {
        this.version_num = version_num;
    }


    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }


    public String getOwnerusername() {
        return ownerusername;
    }

    public void setOwnerusername(String ownerusername) {
        this.ownerusername = ownerusername;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
