package com.csipsimple.f5chat.bean;

/**
 * Created by HP on 20-06-2016.
 */
public class Contact
{
    String name;
    String number;
    String JID;
    String image;
    String type;
    String owner;
    String countryCode;

    public Contact(String name, String number, String JID, String image, String type,String owner) {
        this.name = name;
        this.number = number;
        this.JID = JID;
        this.image = image;
        this.type = type;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getJID() {
        return JID;
    }

    public void setJID(String JID) {
        this.JID = JID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

