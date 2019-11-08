package com.tas.beaconzz.Attend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//출석 조회 액티비티
public class AttendActivity extends AppCompatActivity {
    private  String checkTitle;
    private  String checkDivide;
    private  String checkTime;
    private ListView attendListView; //강의 내역을 파싱하기 위한 것들
    private AttendListAdapter adapter;
    private List<Attend> attendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        TextView courseName = (TextView) findViewById(R.id.courseName);
        TextView courseTime = (TextView) findViewById(R.id.courseTime);
        checkTitle = getIntent().getStringExtra("checkTitle");
        checkDivide = getIntent().getStringExtra("checkDivide");
        checkTime = getIntent().getStringExtra("checkTime");
        attendListView = (ListView)  findViewById(R.id.attendListView);
        attendList = new ArrayList<Attend>();
        adapter = new AttendListAdapter(getApplicationContext(),attendList);
        attendListView.setAdapter(adapter);
        courseName.setText(checkTitle);
        courseTime.setText(checkTime);
        new BackgroundTask().execute();

    }
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;
        @Override
        protected void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/AttendListStudent.php?courseTitle="
                        + URLEncoder.encode(checkTitle+checkDivide, "UTF-8")
                        + "&userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8");

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
                attendList.clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String attendDate, attendDance, attendState, attendStart, attendEnd;
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    attendDate = object.getString("attendDate");
                    attendDance = object.getString("attendDance");
                    attendState = object.getString("attendState");
                    attendStart = object.getString("attendStart");
                    attendEnd = object.getString("attendTime");
                    Attend attend = new Attend(attendDate, attendDance, attendState, attendStart, attendEnd);
                    attendList.add(attend);
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
