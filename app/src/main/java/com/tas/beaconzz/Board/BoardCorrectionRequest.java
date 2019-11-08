package com.tas.beaconzz.Board;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


//게시판을 수정했을때 수정된 값을 전송하는 곳
public class BoardCorrectionRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/CorrectionBoard.php";
    private Map<String, String> parameters;
    //해당 URL을 파라메터들을 POST방식으로 보내준다.
    public BoardCorrectionRequest(String userName , String boardTitle, String boardContent, String boardDate,String correctionNumber, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("userName", userName);
        parameters.put("boardTitle", boardTitle);
        parameters.put("boardContent", boardContent);
        parameters.put("boardDate", boardDate);
        parameters.put("correctionNumber", correctionNumber);


    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
