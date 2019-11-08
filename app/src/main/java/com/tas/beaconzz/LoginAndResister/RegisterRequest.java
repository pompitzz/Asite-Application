package com.tas.beaconzz.LoginAndResister;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
// 아이디 비밀번호등을 보내서 회원가입을 시키기 위한것
public class RegisterRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/Register.php";
    private Map<String, String> parameters;
    //해당 URL을 파라메터들을 POST방식으로 보내준다.
    public RegisterRequest(String userID , String userPassword, String userName, String userEmail , String userMajor, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userEmail", userEmail);
        parameters.put("userMajor", userMajor);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
