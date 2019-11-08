package com.tas.beaconzz.LoginAndResister;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
//로그인 하기 위한 것
public class LoginRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/Login.php";
    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}