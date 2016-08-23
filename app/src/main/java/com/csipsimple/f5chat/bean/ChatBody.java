package com.csipsimple.f5chat.bean;

import java.io.Serializable;

/**
 * Created by Kashish1 on 7/5/2016.
 */
public class ChatBody implements Serializable{
    private String OwnerJID;
    private String FriendJID;
    private String MessageID;
    private String Message;
    private String MessageType;
    private String InOutBound;
    private String Date;
    private String TimeStamp;
    private String Progress;
    private String Status;
    private String Category;
    private String MsgFrom;
    private String contactName;

    private String url;
    private String localUri;
    private String extension;
    private String thumb;

    private String audio_title;
    private String cName;
    private String cImage;
    private long cNumber;

    public ChatBody() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcImage() {
        return cImage;
    }

    public void setcImage(String cImage) {
        this.cImage = cImage;
    }

    public long getcNumber() {
        return cNumber;
    }

    public void setcNumber(long cNumber) {
        this.cNumber = cNumber;
    }

    public String getAudio_title() {
        return audio_title;
    }

    public void setAudio_title(String audio_title) {
        this.audio_title = audio_title;
    }





    public String getMsgFrom() {
        return MsgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        MsgFrom = msgFrom;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }


    public String getOwnerJID() {
        return OwnerJID;
    }

    public void setOwnerJID(String ownerJID) {
        OwnerJID = ownerJID;
    }

    public String getFriendJID() {
        return FriendJID;
    }

    public void setFriendJID(String friendJID) {
        FriendJID = friendJID;
    }

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getInOutBound() {
        return InOutBound;
    }

    public void setInOutBound(String inOutBound) {
        InOutBound = inOutBound;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
