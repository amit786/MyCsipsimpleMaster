package com.csipsimple.f5chat.bean;

/**
 * Created by User on 7/27/2016.
 */
public class GroupNotify
{
    private String groupJID;
    private String groupName;
    private String groupImageUrl;
    private String groupAdmin;
    private String groupSubject;
    private String groupUpdateBy;
    private String membersJID;

    public GroupNotify() {
    }

    public String getMembersJID() {
        return membersJID;
    }

    public void setMembersJID(String membersJID) {
        this.membersJID = membersJID;
    }

    public String getGroupUpdateBy() {
        return groupUpdateBy;
    }

    public void setGroupUpdateBy(String groupUpdateBy) {
        this.groupUpdateBy = groupUpdateBy;
    }

    public String getGroupJID() {
        return groupJID;
    }

    public void setGroupJID(String groupJID) {
        this.groupJID = groupJID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public String getGroupSubject() {
        return groupSubject;
    }

    public void setGroupSubject(String groupSubject) {
        this.groupSubject = groupSubject;
    }
}
