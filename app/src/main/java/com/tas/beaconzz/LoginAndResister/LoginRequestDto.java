package com.tas.beaconzz.LoginAndResister;

import org.json.JSONException;
import org.json.JSONObject;

class LoginRequestDto {

    static JSONObject createJson(String studentId, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
