package com.tas.beaconzz.LoginAndResister;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
//회원가입이 가능한지 체크하는 것(아이디를 사이트에 보내서 회원가입이 가능한지 요청 한것)
public class ValidateRequest extends StringRequest {
    final static private String URL = "http://ec2-15-164-143-254.ap-northeast-2.compute.amazonaws.com/student/join/validate";

    public ValidateRequest(String userID ,  Response.Listener<String> listener) {
        super(Method.POST , URL , listener, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

