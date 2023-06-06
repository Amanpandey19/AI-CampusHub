package com.eCampusNITK.Models;

public class ChatMessage {
    String senderID;
    String receiverId;
    String actualMessage;
    String timeOfMessage;
    int    typeofMessage;
    // typeOfMessage = 0  , Chat is created , no message is there.
    // typeOfMessage = 1  , Message is text
    // typeOfMessage = 2  , Message is pdf
    // typeOfMessage = 3  , Message is imageFromCameraOrGallery

    String fileName;
    int    messageStatus;
    // messageStatus = 0, message is sent;
    // messageStatus = 1, message is delivered;
    // messageStatus = 2, message is seen;
    String fileUrl;

    public ChatMessage() {
    }

    public ChatMessage(String senderID, String receiverId, String actualMessage, String timeOfMessage, int typeofMessage, String fileName, int messageStatus, String fileUrl) {
        this.senderID = senderID;
        this.receiverId = receiverId;
        this.actualMessage = actualMessage;
        this.timeOfMessage = timeOfMessage;
        this.typeofMessage = typeofMessage;
        this.fileName = fileName;
        this.messageStatus = messageStatus;
        this.fileUrl = fileUrl;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getActualMessage() {
        return actualMessage;
    }

    public void setActualMessage(String actualMessage) {
        this.actualMessage = actualMessage;
    }

    public String getTimeOfMessage() {
        return timeOfMessage;
    }

    public void setTimeOfMessage(String timeOfMessage) {
        this.timeOfMessage = timeOfMessage;
    }

    public int getTypeofMessage() {
        return typeofMessage;
    }

    public void setTypeofMessage(int typeofMessage) {
        this.typeofMessage = typeofMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
