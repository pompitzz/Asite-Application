package com.tas.beaconzz.Board;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
//게시판에 댓글을 작성하였을때 댓글 내용을 전송하는 곳
public class ReplyRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/Reply.php";
    private Map<String, String> parameters;
    //해당 URL을 파라메터들을 POST방식으로 보내준다.
    public ReplyRequest(String userName , String replyContent, String replyDate, String boardNumber, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("userName", userName);
        parameters.put("replyContent", replyContent);
        parameters.put("replyDate", replyDate);
        parameters.put("boardNumber", boardNumber);

    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
