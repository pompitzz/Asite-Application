package com.tas.beaconzz.LoginAndResister;

import org.json.JSONException;
import org.json.JSONObject;

class LoginRequestDto {

    static JSONObject createJson(String studentNumber, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentNumber", studentNumber);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
