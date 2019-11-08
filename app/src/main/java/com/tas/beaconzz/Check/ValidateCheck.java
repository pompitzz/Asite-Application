package com.tas.beaconzz.Check;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//출석체크를 강의당 하루에 한번만 가능하게 하여 혼동을 막기 위한곳
public class ValidateCheck extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/CheckValidate.php";
    private Map<String, String> parameters;

    public ValidateCheck(String userID ,String courseTitle, String monthDay, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("courseTitle", courseTitle);
        parameters.put("updateTitle", courseTitle.substring(0,courseTitle.length()-3));
        parameters.put("monthDay", monthDay);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
