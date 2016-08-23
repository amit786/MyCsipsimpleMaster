package com.csipsimple.f5chat.bean;

/**
 * Created by Administrator on 7/22/2016.
 */
public class GroupMember {

    private String member_jid;
    private String is_admin;
    private String image_url;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_jid() {
        return member_jid;
    }

    public void setMember_jid(String member_jid) {
        this.member_jid = member_jid;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
