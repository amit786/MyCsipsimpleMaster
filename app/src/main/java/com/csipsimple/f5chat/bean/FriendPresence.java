package com.csipsimple.f5chat.bean;

/**
 * Created by User on 7/28/2016.
 */
public class FriendPresence
{
    String friendJID;
    String Type;
    String Mode;

    public FriendPresence() {
    }

    public String getFriendJID() {
        return friendJID;
    }

    public void setFriendJID(String friendJID) {
        this.friendJID = friendJID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }
}
