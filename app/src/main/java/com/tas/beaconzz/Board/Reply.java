package com.tas.beaconzz.Board;
//각각 게시판의 댓글들을 받아오는 곳
public class Reply {
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

    public Reply(String replyContent, String replyDate, String replyName, String replyNumber) {
        this.replyContent = replyContent;
        this.replyDate = replyDate;
        this.replyName = replyName;
        this.replyNumber = replyNumber;
    }
}