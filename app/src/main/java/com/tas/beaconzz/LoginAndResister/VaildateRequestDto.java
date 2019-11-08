package com.tas.beaconzz.LoginAndResister;

import org.json.JSONException;
import org.json.JSONObject;

class VaildateRequestDto {

    static JSONObject createJson(String studentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
