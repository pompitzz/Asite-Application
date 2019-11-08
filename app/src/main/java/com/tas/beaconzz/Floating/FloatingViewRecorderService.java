package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
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

import com.melnykov.fab.FloatingActionButton;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import java.io.File;


public class FloatingViewRecorderService extends Service implements View.OnTouchListener{

    private String TAG = "FloatingViewService";
    private View onTopView;
    private WindowManager manager;
    private String GetTime;
    private static final int REQ_CODE_OVERLAY_PERMISSION = 1;

    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button
    private MainActivity mContent;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.floating_recorder, null);
        onTopView.setOnTouchListener(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.CENTER;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(onTopView, params);
        ImageView btnClose = onTopView.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                                manager.removeView(onTopView);
                                onTopView = null;
                                stopSelf(); }});
        mChronometer = (Chronometer) onTopView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) onTopView.findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) onTopView.findViewById(R.id.btnRecord);
        mRecordButton.setColorNormal(getResources().getColor(R.color.primary));
        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

    }
    // Recording Start/Stop
    private void onRecord(boolean start){

        if (start) {
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(this,"녹음을 시작합니다.",Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/테스트1");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText("녹음중.");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText("녹음중..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText("녹음중...");
                        mRecordPromptCount = -1;
                    }mRecordPromptCount++;
                }
            });
            //start RecordingService
            if(Settings.canDrawOverlays(this)){
                Intent intent = new Intent(FloatingViewRecorderService.this, RecordingService.class);
                FloatingViewRecorderService.this.startService(intent);
              //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
            }
            //keep screen on while recording
            mRecordingPrompt.setText("녹음중.");
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText("버튼 클릭시 녹음 시작");
            if(Settings.canDrawOverlays(this)){
                Intent intent = new Intent(FloatingViewRecorderService.this, RecordingService.class);
                FloatingViewRecorderService.this.stopService(intent);
               // (mContent).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                (mContent).startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
            }
            //allow the screen to turn off again once recording is finished
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String result = intent.getStringExtra("textGo");
        return startId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onTopView != null) {
            manager.removeView(onTopView);
            onTopView = null;
        }
    }

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
