package com.example.blackphototatoo;

public class Users {
    private String UidPhotoUrl;
    private String Uid;
    private String name;
    private String mail;
    private Boolean premium;
    public Users() {
    }



    public Users(String uidPhotoUrl, String uid, String name, String mail, boolean premium) {
        UidPhotoUrl = uidPhotoUrl;
        Uid = uid;
        this.name = name;
        this.mail = mail;
        this.premium = premium;
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

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }
}
