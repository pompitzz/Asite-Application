package com.tas.beaconzz.LoginAndResister;

import org.json.JSONException;
import org.json.JSONObject;

public class ResisterRequestDto{

    static JSONObject createJson(String studentId, String password, String name, String email, String major) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("major", major);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
