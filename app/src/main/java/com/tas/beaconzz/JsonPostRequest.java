package com.tas.beaconzz;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

//회원가입이 가능한지 체크하는 것(아이디를 사이트에 보내서 회원가입이 가능한지 요청 한것)
public class JsonPostRequest extends JsonObjectRequest {

    public JsonPostRequest(String url ,@Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
            super(Method.POST, MyServer.url + url ,jsonRequest , listener, null);
    }
}