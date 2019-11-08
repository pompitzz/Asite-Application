package com.tas.beaconzz.Tip;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tas.beaconzz.R;

public class TipActivity extends AppCompatActivity {
    public static RelativeLayout fra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 현재 스마트폰 화면을 세로로 고정
        fra  = (RelativeLayout) findViewById(R.id.fragment);
        final ImageView atmButton = (ImageView) findViewById(R.id.atmButton);
        final ImageView libraryButton = (ImageView) findViewById(R.id.libraryButton);
        final ImageView musicButton = (ImageView) findViewById(R.id.musicButton);
        final ImageView healthButton = (ImageView) findViewById(R.id.healthButton);
        final ImageView copyroomButton = (ImageView) findViewById(R.id.copyroomButton);
        final ImageView cafeButton = (ImageView) findViewById(R.id.cafeButton);
        final ImageView convenienceButton = (ImageView) findViewById(R.id.convenienceButton);
        final ImageView accommodationButton = (ImageView) findViewById(R.id.accommodationButton);
        final ImageView councilButton = (ImageView) findViewById(R.id.councilButton);
        final ImageView cafeteriaButton = (ImageView) findViewById(R.id.cafeteriaButton);

        new Click(atmButton, new TipmapFragment(), "ATM");
        new Click(libraryButton, new TipmapFragment(), "도서관");
        new Click(musicButton, new TipmapFragment(), "음악감상실");
        new Click(healthButton, new TipmapFragment(), "보건소");
        new Click(copyroomButton, new TipmapFragment(), "복사실");
        new Click(cafeButton, new TipmapFragment(), "카페");
        new Click(convenienceButton, new TipmapFragment(), "편의점");
        new Click(accommodationButton, new TipmapFragment(), "편의시설");
        new Click(councilButton, new TipmapFragment(), "학생회실");
        new Click(cafeteriaButton, new TipmapFragment(), "학식");

    }
    private class Click{
        Click(ImageView view, final Fragment fragment, final String category){
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putString("category",category);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragment.setArguments(args);
                            fragmentTransaction.replace(R.id.fragment, fragment);
                            fragmentTransaction.commit();
                            fra.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

}
