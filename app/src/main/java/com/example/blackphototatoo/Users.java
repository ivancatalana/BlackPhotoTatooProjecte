package com.example.blackphototatoo;

public class Users {
    private String UidPhotoUrl;
    private String Uid;
    private String name;
    private String mail;

    public Users() {
    }

    public Users(String uidPhotoUrl, String uid, String name, String mail) {
        UidPhotoUrl = uidPhotoUrl;
        Uid = uid;
        this.name = name;
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public String getUidPhotoUrl() {
        return UidPhotoUrl;
    }

    public String getUid() {
        return Uid;
    }

    public String getName() {
        return name;
    }
}
