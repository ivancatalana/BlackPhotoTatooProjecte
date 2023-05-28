package com.example.blackphototatoo;

public class PremiumUser {
    private String name;
    private String profileImageUrl;
    private String email;
    private String uid;
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public PremiumUser(String name, String profileImageUrl, String email,String uid, String location) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.uid = uid;
        this.location = location;
    }

    // Constructor, getters y setters
    // ...
}