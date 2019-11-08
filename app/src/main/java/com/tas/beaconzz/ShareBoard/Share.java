package com.tas.beaconzz.ShareBoard;
//Couerse 값(db)
public class Share {
    String boardTitle;
    String boardContent;
    String boardDate;
    String boardName;
    String boardNumber;
    String boardImageUrl;

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public String getBoardDate() {
        return boardDate;
    }

    public void setBoardDate(String boardDate) {
        this.boardDate = boardDate;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardNumber() {
        return boardNumber;
    }

    public String getBoardImageUrl() {
        return boardImageUrl;
    }

    public void setBoardImageUrl(String boardImageUrl) {
        this.boardImageUrl = boardImageUrl;
    }

    public void setBoardNumber(String boardNumber) {
        this.boardNumber = boardNumber;
    }

    public Share(String boardTitle, String boardContent, String boardDate, String boardName, String boardNumber, String boardImageUrl) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardDate = boardDate;
        this.boardName = boardName;
        this.boardNumber = boardNumber;
        this.boardImageUrl = boardImageUrl;
    }
}