package com.tas.beaconzz.Board;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//게시판을 삭제할때 그 게시판의 고유 정보를 보내 삭제를 요청하는곳
public class BoardDeleteRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/BoardDelete.php";
    private Map<String, String> parameters;
    public BoardDeleteRequest(String boardNumber, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("boardNumber", boardNumber);

    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
