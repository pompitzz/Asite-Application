package com.tas.beaconzz.Board;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
//게시판에서 수정버튼 클릭시 현재 게시판의 내용들이 게시판 작성하는 layout으로 넘어가 게시판을 수정할수 있는 Activity
public class WriteCorrectionActivity extends AppCompatActivity {

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
        TextView changeText = (TextView) findViewById(R.id.changeText);
        changeText.setText("건의사항 게시글 수정");
        writeCompletion.setText("수정");
        String correctionTitle = getIntent().getStringExtra("boardTitle");
        String correctionContent = getIntent().getStringExtra("boardContent");
        final String correctionNumber = getIntent().getStringExtra("boardNumber");
        titleText.setText(correctionTitle); contentText.setText(correctionContent);

        writeCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String boardTitle = titleText.getText().toString();
                final String boardContent = contentText.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(WriteCorrectionActivity.this);
                                builder.setMessage("수정완료")
                                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface,int which){
                                                Intent intent = new Intent(WriteCorrectionActivity.this, BoardContent.class);
                                                intent.putExtra("boardTitle",boardTitle);
                                                intent.putExtra("boardContent",boardContent);
                                                intent.putExtra("userName", MainActivity.userIDName);
                                                intent.putExtra("boardDate",boardDate);
                                                intent.putExtra("boardNumber",correctionNumber);
                                                WriteCorrectionActivity.this.startActivity(intent);
                                                finish();
                                            }
                                        }).setCancelable(false).create()
                                        .show();
                            } }
                        catch (JSONException e){
                            e.printStackTrace(); } }};
                BoardCorrectionRequest boardCorrectionRequest= new BoardCorrectionRequest(MainActivity.userIDName, boardTitle, boardContent,boardDate,correctionNumber, responseListener);
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                queue.add(boardCorrectionRequest);
            }
        });
    }
}
