package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.melnykov.fab.FloatingActionButton;
import com.tas.beaconzz.Check.AddCheck;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class FloatingViewYesNoService extends Service implements View.OnTouchListener{

    private String TAG = "FloatingViewService";
    private View onTopView;
    private WindowManager manager;
    private Button yesButton, noButton;
    private TextView attendStart, attendEnd, attendDance, attendState;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.floating_yesno, null);
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

        noButton = (Button) onTopView.findViewById(R.id.noButton);
        yesButton = (Button) onTopView.findViewById(R.id.yesButton);
        attendStart = (TextView) onTopView.findViewById(R.id.attendStart);
        attendEnd = (TextView) onTopView.findViewById(R.id.attendEnd);
        attendDance = (TextView) onTopView.findViewById(R.id.attendDance);
        attendState = (TextView) onTopView.findViewById(R.id.attendState);


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent  = new Intent(FloatingViewYesNoService.this, FloatingViewService.class);
                FloatingViewYesNoService.this.startService(intent);
                                manager.removeView(onTopView);
                                onTopView = null;
                                stopSelf(); }});

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String getAttendStart = intent.getStringExtra("attendStart");
        String getAttendState = intent.getStringExtra("attendState");
        String getAttendEnd = intent.getStringExtra("attendEnd");
        String getAttendDate = intent.getStringExtra("attendDate");
        String getAttendDance = intent.getStringExtra("attendDance");
        if(getAttendDance.equals("출석")){
            attendDance.setText("출석상태 : 정상 출석");
        }else{
            attendDance.setText("출석상태 : 지각 처리");
        }
        attendEnd.setText("종료시간 : " + getAttendEnd);
        attendStart.setText("출석시간 : " + getAttendStart);
        attendState.setText("종료상태 : " + getAttendState);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                manager.removeView(onTopView);
                                onTopView = null;
                                stopSelf();
                            }
                            else{
                                Toast.makeText(v.getContext(),"실패",Toast.LENGTH_SHORT).show();}
                        }
                        catch (JSONException e){
                            e.printStackTrace(); } }};

                AddCheck addCheck = new AddCheck(MainActivity.userID, MainActivity.userName, MainActivity.userTitle+MainActivity.userDivide,
                        getAttendStart,getAttendEnd, getAttendDate, getAttendDance, getAttendState, responseListener); // 실질적인 접속 명령어
                RequestQueue queue = Volley.newRequestQueue(v.getContext()); //Request를 실질적으로 보내는 Queue
                queue.add(addCheck);
            }});
        return startId;
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
