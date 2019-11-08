package com.tas.beaconzz.Board;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
//게시판의 댓글을 삭제했을때 그 댓글의 고유번호를 전송하여 그 댓글을 삭제하는 곳
public class ReplyDeleteRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/ReplyDelete.php";
    private Map<String, String> parameters;
    public ReplyDeleteRequest(String replyNumber,Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("replyNumber", replyNumber);

    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
