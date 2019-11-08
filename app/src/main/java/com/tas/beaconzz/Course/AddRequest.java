package com.tas.beaconzz.Course;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.tas.beaconzz.NoticeAndMain.MainActivity;

import java.util.HashMap;
import java.util.Map;
//강의를 추가하였을때 시간표 테이블에 학생 및 강의 정보를 전송하기 위한것
public class AddRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/CourseAdd.php";
    private Map<String, String> parameters;

    public AddRequest(String userID , String courseID, String courseTitle, String courseProfessor, String courseTime, String courseDivide, String courseRoom, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("courseID", courseID);
        parameters.put("userName", MainActivity.userName);
        parameters.put("courseTitle", courseTitle);
        parameters.put("courseProfessor", courseProfessor);
        parameters.put("courseTime", courseTime);
        parameters.put("courseDivide", courseDivide);
        parameters.put("courseRoom", courseRoom);
        parameters.put("courseTitleDivide", courseTitle + courseDivide);
        parameters.put("defaultNumber", "0");
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
