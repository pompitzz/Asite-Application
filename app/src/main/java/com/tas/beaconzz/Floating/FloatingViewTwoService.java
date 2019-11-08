package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.tas.beaconzz.Check.BeaconAdapter;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FloatingViewTwoService extends Service implements View.OnTouchListener{
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
        onTopView = inflater.inflate(R.layout.floating_on_top_layout2, null);
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
        Button BreakTime = onTopView.findViewById(R.id.BreakTime);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(FloatingViewTwoService.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + FloatingViewTwoService.this.getPackageName()));
                    (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                } else {
                    Intent intent  = new Intent(FloatingViewTwoService.this, FloatingViewRecorderService.class);
                    FloatingViewTwoService.this.startService(intent);
                }
            }
        });
        BreakTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(FloatingViewTwoService.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + FloatingViewTwoService.this.getPackageName()));
                    (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                } else {
                    Intent intent  = new Intent(FloatingViewTwoService.this, FloatingViewBreakTimeService.class);
                    FloatingViewTwoService.this.startService(intent);
                    manager.removeView(onTopView);
                    onTopView = null;
                    stopSelf();
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(FloatingViewTwoService.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + FloatingViewTwoService.this.getPackageName()));
                    (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                } else {
                    Intent intent  = new Intent(FloatingViewTwoService.this, FloatingViewCameraService.class);
                    FloatingViewTwoService.this.startService(intent);
                }
            }
        });
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
