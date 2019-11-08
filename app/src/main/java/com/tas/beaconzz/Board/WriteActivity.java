package com.tas.beaconzz.Board;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
//게시판을 작성할때 게시판의 내용들을 작성하기 위한 곳
public class WriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        Button writeCompletion = (Button) findViewById(R.id.writeCompletion);
        final EditText titleText = (EditText) findViewById(R.id.titleText);
        final EditText contentText = (EditText) findViewById(R.id.contentText);
        final String boardDate = format1.format(time);

        writeCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                                builder.setMessage("글쓰기 완료")
                                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface,int which){
                                                finish();
                                            }
                                        }).setCancelable(false)
                                        .create()
                                        .show();
                            } }
                        catch (JSONException e){
                            e.printStackTrace(); } }};
                String boardTitle = titleText.getText().toString();
                String boardContent = contentText.getText().toString();
                BoardRequest boardRequest = new BoardRequest(MainActivity.userIDName, boardTitle, boardContent,boardDate, responseListener);
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                queue.add(boardRequest);
            }
        });


    }
}
