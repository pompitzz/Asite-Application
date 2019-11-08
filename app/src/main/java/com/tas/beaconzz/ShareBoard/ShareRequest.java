package com.tas.beaconzz.ShareBoard;

import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ShareRequest extends StringRequest {
    final static private String URL = "http://dm951125.dothome.co.kr/upload.php";
    private Map<String, String> parameters;
    public ShareRequest(String userName , String boardTitle, String boardContent, String boardDate, Bitmap bitmap,String fileName, Response.Listener<String> listener){
        super(Method.POST , URL , listener, null);
        parameters = new HashMap<>(); //파라메터에 각각 값을 넣기 위한 것
        parameters.put("userName", userName);
        parameters.put("boardTitle", boardTitle);
        parameters.put("boardContent", boardContent);
        parameters.put("boardDate", boardDate);
        parameters.put("fileName", fileName);
        if(bitmap==null){
            parameters.put("image", "empty");
        }else{
            String imageData = imageToString(bitmap);
            parameters.put("image", imageData);}
        }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;

    }
}
