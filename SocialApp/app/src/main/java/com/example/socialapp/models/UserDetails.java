package com.example.socialapp.models;
import java.io.Serializable;
import java.security.PublicKey;
public class UserDetails implements Serializable {

    public String username;
    public String firstname;
    public String lastname;
    public String password;
    public String emailid;
    public String mobile;
    public String publicKey;

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getEmailId() {
        return emailid;
    }

    public void setEmailId(String emailid) {
        this.emailid = emailid;
    }

    public String getTelephone() {
        return mobile;
    }

    public void setTelephone(String mobile) {
        this.mobile = mobile;
    }

    public String getUserPublicKey() {
        return publicKey;
    }

    public void setUserPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}