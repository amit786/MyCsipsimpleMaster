package com.csipsimple.f5chat.bean;

/**
 * Created by Administrator on 7/7/2016.
 */
public class RecentChat
{
    String Name;
    String ChatCount;
    String FriendJID;
    String ImageFirst;
    String ImageSec;
    String ImageThird;
    String ImageFour;
    String LastMessage;
    String Time;

    private String group_jid, group_name, group_admin_jid, url;

    public RecentChat(String group_jid, String group_name, String group_admin_jid, String url) {
        this.group_jid = group_jid;
        this.group_name = group_name;
        this.group_admin_jid = group_admin_jid;
        this.url = url;
    }

    public String getGroup_jid() {
        return group_jid;
    }

    public void setGroup_jid(String group_jid) {
        this.group_jid = group_jid;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_admin_jid() {
        return group_admin_jid;
    }

    public void setGroup_admin_jid(String group_admin_jid) {
        this.group_admin_jid = group_admin_jid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RecentChat() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getChatCount() {
        return ChatCount;
    }

    public void setChatCount(String chatCount) {
        ChatCount = chatCount;
    }

    public String getFriendJID() {
        return FriendJID;
    }

    public void setFriendJID(String friendJID) {
        FriendJID = friendJID;
    }

    public String getImageFirst() {
        return ImageFirst;
    }

    public void setImageFirst(String imageFirst) {
        ImageFirst = imageFirst;
    }

    public String getImageSec() {
        return ImageSec;
    }

    public void setImageSec(String imageSec) {
        ImageSec = imageSec;
    }

    public String getImageThird() {
        return ImageThird;
    }

    public void setImageThird(String imageThird) {
        ImageThird = imageThird;
    }

    public String getImageFour() {
        return ImageFour;
    }

    public void setImageFour(String imageFour) {
        ImageFour = imageFour;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
