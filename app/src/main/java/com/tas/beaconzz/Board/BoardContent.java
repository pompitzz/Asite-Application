package com.tas.beaconzz.Board;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;
import com.tas.beaconzz.ShareBoard.ShareContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//건의 사항 게시판에서 원하는 게시물에 접속시 게시판의 내용들을 보여주는 Activity
public class BoardContent extends AppCompatActivity {
    private ListView replyListView;
    private ReplyListAdapter adapter;
    private List<Reply> replyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_content);
        replyListView = (ListView)  findViewById(R.id.replyListView);
        replyList = new ArrayList<Reply>();
        adapter = new ReplyListAdapter(BoardContent.this,replyList,this);
        replyListView.setAdapter(adapter);
        new BoardContent.BackgroundTask().execute();
        final String boardTitle = getIntent().getStringExtra("boardTitle");
        final String boardContent = getIntent().getStringExtra("boardContent");
        final String userName = getIntent().getStringExtra("userName");
        final String boardDate = getIntent().getStringExtra("boardDate");
        final String boardNumber = getIntent().getStringExtra("boardNumber");

        TextView correctionBoard = (TextView) findViewById(R.id.correctionBoard);
        Button replyButton = (Button) findViewById(R.id.replyButton);
        TextView deleteBoard = (TextView) findViewById(R.id.deleteBoard);
        final EditText replyContent= (EditText) findViewById(R.id.replyContent);
        TextView setBoardTitle =(TextView) findViewById(R.id.boardTitle);
        TextView setBoardContent =(TextView) findViewById(R.id.boardContent);
        TextView setUserName =(TextView) findViewById(R.id.userName);
        TextView setBoardDate =(TextView) findViewById(R.id.boardDate);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date time = new Date();
        final String replyDate = format1.format(time);

        setBoardTitle.setText(boardTitle);
        setBoardContent.setText(boardContent);
        setUserName.setText(userName);
        setBoardDate.setText(boardDate.substring(0,10));

        correctionBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.userIDName.equals(userName)){
                Intent intent = new Intent(BoardContent.this, WriteCorrectionActivity.class);
                intent.putExtra("boardTitle",boardTitle);
                intent.putExtra("boardContent",boardContent);
                intent.putExtra("boardNumber",boardNumber);
                BoardContent.this.startActivity(intent);
                finish();}
                else{
                    android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(BoardContent.this);
                    builder.setMessage("게시자만 수정이 가능합니다.")
                            .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface,int which){
                                }
                            }).create()
                            .show();
                }
            }
        });
        deleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.userIDName.equals(userName)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardContent.this);
                    builder.setMessage("게시판을 정말로 삭제하시겠습니까?")
                            .setPositiveButton("예",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface,int which){
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try
                                            {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean success = jsonResponse.getBoolean("success");
                                                if(success){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardContent.this);
                                                    AlertDialog dialog = builder.setMessage("삭제성공")
                                                            .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface,int which){
                                                                    finish();
                                                                }
                                                            }).setCancelable(false)
                                                            .create();
                                                    dialog.show();
                                                } }
                                            catch (JSONException e){
                                                e.printStackTrace(); } }};
                                    BoardDeleteRequest boardDeleteRequest= new BoardDeleteRequest(boardNumber, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(v.getContext());
                                    queue.add(boardDeleteRequest);
                                }
                            }).setNegativeButton("아니오",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface,int which){
                        }
                    }).setCancelable(false)
                            .create()
                            .show();
                }
                else{
                    android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(BoardContent.this);
                    builder.setMessage("게시자만 삭제가 가능합니다.")
                            .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface,int which){
                                }
                            }).create()
                            .show();
                }
            }
        });
        replyButton.setOnClickListener(new View.OnClickListener() {
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
                                replyContent.setText(null);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(replyContent.getWindowToken(), 0);
                                new BoardContent.BackgroundTask().execute();
                            } }
                        catch (JSONException e){
                            e.printStackTrace(); } }};
                String boardContent = replyContent.getText().toString();
                ReplyRequest replyRequest= new ReplyRequest(MainActivity.userIDName, boardContent,replyDate,boardNumber, responseListener);
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                queue.add(replyRequest);
            }
        });

    }

    public void resetGraph(Context context) {
        new BoardContent.BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;
        @Override
        protected void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/ReplyList.php?boardNumber="
                        + URLEncoder.encode(getIntent().getStringExtra("boardNumber"), "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try {
                replyList.clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String replyName;
                String replyContent;
                String replyNumber;
                String replyDate;
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    replyName = object.getString("replyName");
                    replyContent = object.getString("replyContent");
                    replyDate = object.getString("replyDate");
                    replyNumber = object.getString("replyNumber");

                    Reply reply = new Reply(replyContent,  replyDate, replyName, replyNumber);
                    replyList.add(reply);
                    count++;
                }
                adapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
