package com.tas.beaconzz.NoticeAndMain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.FontsContractCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.OkHttpClient;
import com.estimote.coresdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Request;
import com.estimote.coresdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Response;
import com.tas.beaconzz.Attend.VerificationFragment;
import com.tas.beaconzz.Board.BoardFragment;
import com.tas.beaconzz.Check.CheckFragment;
import com.tas.beaconzz.Course.CourseFragment;
import com.tas.beaconzz.R;
import com.tas.beaconzz.Schedule.ScheduleFragment;
import com.tas.beaconzz.ShareBoard.ShareFragment;
import com.tas.beaconzz.Tip.TipActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static String userID, userName, userRoom, userTitle, userDivide, userTime, userIDName;
    public static Long user_Key;
    public RelativeLayout fra;
    private ListView noticeListView;
    private OkHttpClient mClient;
    private LinearLayout notice;

    //블루투스 키기
    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        setContentView(R.layout.activity_main);

        initSetting();

        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("name");
        user_Key = getIntent().getLongExtra("studentId", 0L);
        noticeListView = findViewById(R.id.noticeListView);
        mClient = new OkHttpClient();

        new BackgroundTask().execute();

        Button NoticeButton = findViewById(R.id.pknunotice);
        Button majorNoticeButton = findViewById(R.id.jtnotice);

        ImageView boardButton = findViewById(R.id.boardButton);
        ImageView shareButton = findViewById(R.id.shareButton);
        ImageView courseButton = findViewById(R.id.courseButton);
        ImageView checkButton = findViewById(R.id.checkButton);
        ImageView scheduleButton = findViewById(R.id.scheduleButton);
        ImageView mainButton = findViewById(R.id.mainButton);
        ImageView verificationButton = findViewById(R.id.verificationButton);
        ImageView tipButton = findViewById(R.id.tipButton);

        notice = findViewById(R.id.notice);
        fra = findViewById(R.id.fragment);

        /**
         * 학교 공지사항 바로가기
         */
        NoticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://m.pknu.ac.kr/01_notice/01.jsp"));
            startActivity(intent);
        });

        /**
         * 학과 공지사항 바로가기
         */
        majorNoticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://ice.pknu.ac.kr/07/01.php"));
            startActivity(intent);
        });

        tipButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TipActivity.class);
            MainActivity.this.startActivity(intent);
        });

        new Click(courseButton, new CourseFragment());
        new Click(checkButton, new CheckFragment());
        new Click(scheduleButton, new ScheduleFragment());
        new Click(checkButton, new CheckFragment());
        new Click(boardButton, new BoardFragment());
        new Click(verificationButton, new VerificationFragment());
        new Click(shareButton, new ShareFragment());

        mainButton.setOnClickListener(v -> {
            fra.setVisibility(View.GONE);
            notice.setVisibility(View.VISIBLE);
        });

        createNoticeList();
    }

    private void createNoticeList() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("http://www.pknu.ac.kr/upload/xml/PKNU_Notice.xml")
                        .build();
                Response response = null;
                response = mClient.newCall(request).execute();
                final String xml = response.body().string();
                final List<News> data = parse(xml);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NoticeAdapter adapter = new NoticeAdapter(data);
                        noticeListView.setAdapter(adapter);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initSetting() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 현재 스마트폰 화면을 세로로 고정

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        checkPermissions();  //카메라 권한받기
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionDialog();
        }
    }

    private List<News> parse(String xml) {
        try {
            return new NoticeParser().parse(xml);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class News {
        String title;
        String link;
        String pubDate;
        String description;
        String name;

        @Override
        public String toString() {
            return "News{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", description='" + description + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }


    }

    static class ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView descriptionView;
        TextView nameTextView;
    }


    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) // 1500(1.5)초 이내에 두번눌렀을때 종료
        {
            finish();
        }
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 눌러 종료합니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    private class Click {
        Click(ImageView view, final Fragment fragment) {
            view.setOnClickListener(
                    v -> {
                        notice.setVisibility(View.GONE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, fragment);
                        fragmentTransaction.commit();
                        fra.setVisibility(View.VISIBLE);
                    });
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void showOverlayPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("권한 받기");
        builder.setMessage("다른 앱위에 표시 권한을 수락하여야 출석체크를 할수 있으니 권한을 수락하여 주세요.");
        builder.setPositiveButton("예", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        });
        builder.setOnCancelListener(dialog -> Toast.makeText(this,
                "권한을 부여하여야 출석체크가 가능합니다.",
                Toast.LENGTH_LONG).show());
        builder.show();
    }

    public static class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/ScheduleList.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8");
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
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                Calendar cal = Calendar.getInstance();
                String courseTime;
                String courseTitle;
                String courseDivide;
                String courseRoom;
                userRoom = null;
                userTitle = null;
                userDivide = null;

                String timeCourse, timeStart, timeEnd;
                int i;
                long courseDiff, nowDiff, courseDuration, nowDuration;
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

                String korDay = "";
                switch (dayOfWeek) {
                    case 1:
                        korDay = "일";
                        break;
                    case 2:
                        korDay = "월";
                        break;
                    case 3:
                        korDay = "화";
                        break;
                    case 4:
                        korDay = "수";
                        break;
                    case 5:
                        korDay = "목";
                        break;
                    case 6:
                        korDay = "금";
                        break;
                    case 7:
                        korDay = "토";
                        break;
                }
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getTime = sdf.format(date);

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseTime = object.getString("courseTime");
                    courseTitle = object.getString("courseTitle");
                    courseDivide = object.getString("courseDivide");
                    courseRoom = object.getString("courseRoom");
                    count++;
                    if (courseTime.indexOf(korDay) > -1) {
                        i = courseTime.indexOf(korDay);
                        timeCourse = courseTime.substring(i, courseTime.length() - 1);
                        timeStart = timeCourse.substring(timeCourse.indexOf("(") + 1, timeCourse.indexOf("(") + 6);
                        timeEnd = timeCourse.substring(timeCourse.indexOf("(") + 7, timeCourse.indexOf("(") + 12);
                        Date courseStart = dateFormat.parse(timeStart);
                        Date courseEnd = dateFormat.parse(timeEnd);
                        Date nowTime = dateFormat.parse(getTime);
                        courseDuration = courseEnd.getTime() - courseStart.getTime(); // 글이 올라온시간,현재시간비교
                        nowDuration = nowTime.getTime() - courseStart.getTime(); // 글이 올라온시간,현재시간비교
                        courseDiff = courseDuration / 60000;
                        nowDiff = nowDuration / 60000;
                        if (nowDiff >= -10) {
                            if (nowDiff <= courseDiff - 10) {
                                userRoom = courseRoom;
                                userTitle = courseTitle;
                                userDivide = courseDivide;
                                userTime = courseTime;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
