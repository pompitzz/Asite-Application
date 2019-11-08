package com.tas.beaconzz.Board;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//게시판을 작성할때 게시판의 내용들을 보내 게시판을 생성하는 곳
public class BoardRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/Board.php";
    private Map<String, String> parameters;
    //해당 URL을 파라메터들을 POST방식으로 보내준다.
    public BoardRequest(String userName , String boardTitle, String boardContent,String boardDate, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("userName", userName);
        parameters.put("boardTitle", boardTitle);
        parameters.put("boardContent", boardContent);
        parameters.put("boardDate", boardDate);

    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
