package com.tas.beaconzz.Course;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//DB에 저장되어 있는 강의목록등을 가져 오는곳
@Getter
@Setter
@NoArgsConstructor
public class Course {
    int courseID;
    String courseTerm;
    String courseGrade;
    String courseTitle;
    int courseCredit;
    int courseDivide;
    String courseRoom;
    String courseProfessor;
    String courseTime;

    @Builder
    public Course(int courseID,String courseTerm, String courseGrade, String courseTitle, int courseCredit, int courseDivide, String courseRoom, String courseProfessor, String courseTime) {
        this.courseID = courseID;
        this.courseTerm = courseTerm;
        this.courseGrade = courseGrade;
        this.courseTitle = courseTitle;
        this.courseCredit = courseCredit;
        this.courseDivide = courseDivide;
        this.courseRoom = courseRoom;
        this.courseProfessor = courseProfessor;
        this.courseTime = courseTime;
    }
}