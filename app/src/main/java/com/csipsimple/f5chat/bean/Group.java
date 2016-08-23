package com.csipsimple.f5chat.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/22/2016.
 */
public class Group {

    private String group_name, group_jid, creation_date, time_stamp, admin_jid, group_image_url;
    private ArrayList<GroupMember> groupMember;

    public Group() {
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public ArrayList<GroupMember> getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(ArrayList<GroupMember> groupMember) {
        this.groupMember = groupMember;
    }

    public String getGroup_image_url() {
        return group_image_url;
    }

    public void setGroup_image_url(String group_image_url) {
        this.group_image_url = group_image_url;
    }

    public String getAdmin_jid() {
        return admin_jid;
    }

    public void setAdmin_jid(String admin_jid) {
        this.admin_jid = admin_jid;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getGroup_jid() {
        return group_jid;
    }

    public void setGroup_jid(String group_jid) {
        this.group_jid = group_jid;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }
}
