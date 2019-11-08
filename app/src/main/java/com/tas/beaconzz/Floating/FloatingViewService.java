package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Check.AddCheck;
import com.tas.beaconzz.Check.BeaconAdapter;
import com.tas.beaconzz.Check.CheckFragment;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class    FloatingViewService extends Service implements View.OnTouchListener{
    private static final int REQ_CODE_OVERLAY_PERMISSION = 1;
    private String TAG = "FloatingViewService";
    private View onTopView;
    private WindowManager manager;
    private String attendDance, attendState;
    private MainActivity mContent;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.floating_on_top_layout, null);
        onTopView.setOnTouchListener(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                (int) (getResources().getDisplayMetrics().heightPixels * 0.9),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.CENTER;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(onTopView, params);
        Calendar cal = Calendar.getInstance();
        final String userID = MainActivity.userID;
        final String userName = MainActivity.userName;
        final String courseTitle= MainActivity.userTitle;
        final String courseDivide= MainActivity.userDivide;
        Button checkTime = onTopView.findViewById(R.id.CheckTime);
        ImageView cameraButton = onTopView.findViewById(R.id.cameraButton);
        ImageView recordButton = onTopView.findViewById(R.id.recordButton);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); String korDayOfweek="";
        switch (dayOfWeek){
            case 1:
                korDayOfweek = "일";
                break;
            case 2:
                korDayOfweek = "월";
                break;
            case 3:
                korDayOfweek = "화";
                break;
            case 4:
                korDayOfweek = "수";
                break;
            case 5:
                korDayOfweek = "목";
                break;
            case 6:
                korDayOfweek = "금";
                break;
            case 7:
                korDayOfweek = "토";
                break;
        }
        String timecourse = ""; String timeGet = "12:00"; String timeEnd = "";
        if(MainActivity.userTime.indexOf(korDayOfweek)>-1){
            int i = MainActivity.userTime.indexOf(korDayOfweek);
            timecourse = MainActivity.userTime.substring(i,MainActivity.userTime.length()-1);
            timeGet = timecourse.substring(timecourse.indexOf("(")+1,timecourse.indexOf("(")+6);
            timeEnd = timecourse.substring(timecourse.indexOf("(")+7,timecourse.indexOf("(")+12);
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(FloatingViewService.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +FloatingViewService.this.getPackageName()));
                    (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                } else {
                    Intent intent  = new Intent(FloatingViewService.this, FloatingViewRecorderService.class);
                    FloatingViewService.this.startService(intent);
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(FloatingViewService.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +FloatingViewService.this.getPackageName()));
                    (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                } else {
                    Intent intent  = new Intent(FloatingViewService.this, FloatingViewCameraService.class);
                    FloatingViewService.this.startService(intent);
                }
            }
        });
        String finalTimeGet = timeGet;
        String finalTimeEnd = timeEnd;
        checkTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");  String time = BeaconAdapter.startTime;
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getTime = sdf.format(date);
                try {
                    Date d1 = dateFormat.parse(finalTimeGet);
                    Date d2 = dateFormat.parse(time.substring(11,16));
                    Date d3 = dateFormat.parse(finalTimeEnd);
                    Date d4 = dateFormat.parse(getTime);
                    long duration = d2.getTime() - d1.getTime();
                    long durationEnd  = d3.getTime() - d4.getTime();
                    long min = duration/60000;
                    long minEnd = durationEnd/60000;
                    if (min>5) {  attendDance = "지각";
                    }else{
                        attendDance = "출석";
                    }
                    if (minEnd>5) {  attendState = "빠른 종료";
                    }else{
                        attendState = "정상 종료";
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Intent intent  = new Intent(FloatingViewService.this, FloatingViewYesNoService.class);
                intent.putExtra("attendStart",time.substring(11,16));
                intent.putExtra("attendEnd",getTime);
                intent.putExtra("attendDance",attendDance);
                intent.putExtra("attendState",attendState);
                intent.putExtra("attendDate",time.substring(5,10));
                FloatingViewService.this.startService(intent);
                manager.removeView(onTopView);
                onTopView = null;
                stopSelf();
            }
        });
//        checkTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try
//                        {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            boolean success = jsonResponse.getBoolean("success");
//
//                            if (success) {
//                                manager.removeView(onTopView);
//                                onTopView = null;
//                                stopSelf();
//                            }
//                            else{
//                                Toast.makeText(v.getContext(),"실패",Toast.LENGTH_SHORT).show();}
//                        }
//                        catch (JSONException e){
//                            e.printStackTrace(); } }};
//
//                AddCheck addCheck = new AddCheck(userID, userName, courseTitle+courseDivide, time.substring(11,16),getTime, time.substring(5,10), attendDance, attendState, responseListener); // 실질적인 접속 명령어
//                RequestQueue queue = Volley.newRequestQueue(v.getContext()); //Request를 실질적으로 보내는 Queue
//                queue.add(addCheck);
//            }
//        });
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (onTopView != null) {
//            manager.removeView(onTopView);
//            onTopView = null;
//        }
//    }

    float xpos = 0;
    float ypos = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int action = motionEvent.getAction();
        int pointerCount = motionEvent.getPointerCount();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (pointerCount == 1) {
                    xpos = motionEvent.getRawX();
                    ypos = motionEvent.getRawY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
                    float dx = xpos - motionEvent.getRawX();
                    float dy = ypos - motionEvent.getRawY();
                    xpos = motionEvent.getRawX();
                    ypos = motionEvent.getRawY();

                    Log.d(TAG, "lp.x : " + lp.x + ", dx : " + dx + "lp.y : " + lp.y + ", dy : " + dy);

                    lp.x = (int) (lp.x - dx);
                    lp.y = (int) (lp.y - dy);

                    manager.updateViewLayout(view,lp);
                    return true;
                }
                break;

        }
        return false;
    }

}
