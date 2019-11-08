package com.tas.beaconzz.Schedule;

public class GetSchedule {
    String courseDivide;
    String courseTitle;
    String courseProfessor;
    String courseTime;

    public String getCourseDivide() {
        return courseDivide;
    }

    public void setCourseDivide(String courseDivide) {
        this.courseDivide = courseDivide;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseProfessor() {
        return courseProfessor;
    }

    public void setCourseProfessor(String courseProfessor) {
        this.courseProfessor = courseProfessor;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public GetSchedule(String courseDivide, String courseTitle, String courseProfessor, String courseTime){
        this.courseDivide = courseDivide;
        this.courseTitle = courseTitle;
        this.courseProfessor = courseProfessor;
        this.courseTime = courseTime;
    }
}
