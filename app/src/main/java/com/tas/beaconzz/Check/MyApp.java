package com.tas.beaconzz.Check;

import android.app.Application;
import android.util.Log;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.service.BeaconManager;

/**
 * Created by yudizsolutionspvtltd on 27/03/18.
 */

//비콘을 스캔할때 비콘 스캔 주기 등을 설정하는 곳
public class MyApp extends Application {

    public BeaconManager beaconManager;
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        EstimoteSDK.initialize(this, "", "");
        EstimoteSDK.enableDebugLogging(true);
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(100, 100);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "onServiceReady: ");
            }

        });
        beaconManager.setErrorListener(new BeaconManager.ErrorListener() {
            @Override
            public void onError(Integer errorId) {
                Log.e(TAG, "onError: " + errorId);
            }
        });
    }
}
