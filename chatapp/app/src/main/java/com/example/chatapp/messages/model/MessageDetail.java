package com.example.chatapp.messages.model;

public class MessageDetail {
    private String senderUID;
    private String receiverUID;
    private String content;
    private Long sendTime;
    private String type ="";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageDetail() {
    }

    public MessageDetail(String senderUID, String receiverUID, String content, Long sendTime) {
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.content = content;
        this.sendTime = sendTime;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
