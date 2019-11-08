package com.tas.beaconzz.ShareBoard;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ShareDeleteRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/BoardShareDelete.php";
    private Map<String, String> parameters;
    public ShareDeleteRequest(String boardNumber, String deleteImage, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("boardNumber", boardNumber);
        parameters.put("deleteImage", deleteImage);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
