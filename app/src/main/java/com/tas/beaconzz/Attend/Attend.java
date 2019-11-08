package com.tas.beaconzz.Attend;
//출석 조회시 불러올 값들
public class Attend {

    String attendDate;
    String attendDance;
    String attendState;
    String attendStart;

    public String getAttendStart() {
        return attendStart;
    }

    public void setAttendStart(String attendStart) {
        this.attendStart = attendStart;
    }

    public String getAttendEnd() {
        return attendEnd;
    }

    public void setAttendEnd(String attendEnd) {
        this.attendEnd = attendEnd;
    }

    String attendEnd;

    public String getAttendDance() {
        return attendDance;
    }

    public void setAttendDance(String attendDance) {
        this.attendDance = attendDance;
    }

    public String getAttendState() {
        return attendState;
    }

    public void setAttendState(String attendState) {
        this.attendState = attendState;
    }

    public String getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(String attendDate) {
        this.attendDate = attendDate;
    }

    public Attend(String attendDate, String attendDance, String attendState, String attendStart, String attendEnd) {

        this.attendState = attendState;
        this.attendDance = attendDance;
        this.attendDate = attendDate;
        this.attendStart = attendStart;
        this.attendEnd = attendEnd;

    }
}