package com.tas.beaconzz.Schedule;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ScheduleDeleteRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/ScheduleDelete.php";
    private Map<String, String> parameters;

    public ScheduleDeleteRequest(String userID , String courseTitle, String courseDivide, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("courseTitle", courseTitle);
        parameters.put("courseDivide", courseDivide);
        parameters.put("courseTitleDivide", courseTitle + courseDivide);

    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
