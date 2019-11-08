package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Check.AddCheck;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

//쉬는 시간 버튼 클릭시 10분동안 타이머를 잰 후 다시 어플 제한 화면으로 전환하는 FloatingView
public class FloatingViewBreakTimeService extends Service implements View.OnTouchListener{

    private String TAG = "FloatingViewService";
    private View onTopView;
    private WindowManager manager;
    private TextView textView;
    private Handler mHandler, handler;
    private Runnable mRunnable;
    private long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private int Seconds, Minutes;
    private Button endBreak;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.floating_timer, null);
        onTopView.setOnTouchListener(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(onTopView, params);

        endBreak = (Button) onTopView.findViewById(R.id.endBreak);
        textView = (TextView) onTopView.findViewById(R.id.textView);
        handler = new Handler() ;
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent  = new Intent(FloatingViewBreakTimeService.this, FloatingViewService.class);
                FloatingViewBreakTimeService.this.startService(intent);
                manager.removeView(onTopView);
                onTopView = null;
                stopSelf();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000*60*10);

        endBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent  = new Intent(FloatingViewBreakTimeService.this, FloatingViewService.class);
                FloatingViewBreakTimeService.this.startService(intent);
                                manager.removeView(onTopView);
                                onTopView = null;
                                stopSelf(); }});
    }
    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            textView.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds));
            handler.postDelayed(this, 0);
        }
    };
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mHandler.removeCallbacks(mRunnable);
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
