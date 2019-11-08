package com.tas.beaconzz.ShareBoard;
//Couerse 값(db)
public class ReplyShare {
    String replyContent;
    String replyDate;
    String replyName;
    String replyNumber;
    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyNumber() {
        return replyNumber;
    }

    public void setReplyNumber(String replyNumber) {
        this.replyNumber = replyNumber;
    }

    public ReplyShare(String replyContent, String replyDate, String replyName, String replyNumber) {
        this.replyContent = replyContent;
        this.replyDate = replyDate;
        this.replyName = replyName;
        this.replyNumber = replyNumber;
    }
}