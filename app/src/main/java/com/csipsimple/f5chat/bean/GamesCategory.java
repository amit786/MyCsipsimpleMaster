package com.csipsimple.f5chat.bean;

import java.io.Serializable;

/**
 * Created by SHRIG on 7/19/2016.
 */

public class GamesCategory implements Serializable {

    String imageUrl;
    String appName;
    String caption;
    String downloadLink;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
