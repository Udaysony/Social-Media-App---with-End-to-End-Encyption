package com.example.socialapp.models;

import androidx.lifecycle.ViewModel;

public class Current_Session extends ViewModel {
    public String username;

    public String getUsername() {
        return username;

    }

    public void setUsername(String username) {
        this.username = username;
    }
}
