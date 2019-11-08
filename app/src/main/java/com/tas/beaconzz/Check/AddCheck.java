package com.tas.beaconzz.Check;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
//학생이 출석체크 완료시 출석체크 확인에 필요한 값들을 전송하는곳
public class AddCheck extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/CheckAdd.php";
    private Map<String, String> parameters;

    public AddCheck(String userID , String userName, String courseTitle, String attendStart,String attendEnd, String monthDay, String attendDance, String attendState, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userName", userName);
        parameters.put("courseTitle", courseTitle);
        parameters.put("attendEnd", attendEnd);
        parameters.put("attendStart", attendStart);
        parameters.put("attendDance", attendDance);
        parameters.put("attendState", attendState);
        parameters.put("courseDivide", courseTitle.substring(courseTitle.length()-3,courseTitle.length()-0));

        parameters.put("updateTitle", courseTitle.substring(0,courseTitle.length()-3));
        parameters.put("monthDay", monthDay);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
