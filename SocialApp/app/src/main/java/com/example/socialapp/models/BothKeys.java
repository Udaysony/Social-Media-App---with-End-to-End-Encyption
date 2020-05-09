package com.example.socialapp.models;

import java.io.Serializable;

public class BothKeys {
    public String pub_key;
    public String group_key;

    public String getPub_key() {
        return pub_key;
    }

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public String getGroup_key() {
        return group_key;
    }

    public void setGroup_key(String group_key) {
        this.group_key = group_key;
    }
}

