package com.csipsimple.f5chat.bean;

import java.io.Serializable;

/**
 * Created by Kashish1 on 7/20/2016.
 */
public class StickerCategory implements Serializable {
    String imgUrl;
    String stickerName;
    String stickerSize;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStickerName() {
        return stickerName;
    }

    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }

    public String getStickerSize() {
        return stickerSize;
    }

    public void setStickerSize(String stickerSize) {
        this.stickerSize = stickerSize;
    }
}
