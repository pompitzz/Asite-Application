package com.tas.beaconzz.ShareBoard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Board.BoardContent;
import com.tas.beaconzz.Board.BoardCorrectionRequest;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShareWriteCorrectionActivity extends AppCompatActivity {
    final int CODE_GALLEY_REQUEST = 999;
    Button btnSelection, btnMatrix;
    ImageView imageUpload;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharewrite);
        btnSelection = (Button) findViewById(R.id.btnSelection);
        imageUpload = (ImageView) findViewById(R.id.imageUpload);
        btnMatrix = (Button) findViewById(R.id.btnMatrix);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        Button writeCompletion = (Button) findViewById(R.id.writeCompletion);
        TextView changeText = (TextView) findViewById(R.id.changeText);

        final EditText titleText = (EditText) findViewById(R.id.titleText);
        final EditText contentText = (EditText) findViewById(R.id.contentText);
        final String boardDate = format1.format(time);
        changeText.setText("헌책 나눔 게시글 수정");
        writeCompletion.setText("수정");
        String correctionTitle = getIntent().getStringExtra("boardTitle");
        String correctionContent = getIntent().getStringExtra("boardContent");
        final String correctionNumber = getIntent().getStringExtra("boardNumber");
        final String correctionUri = getIntent().getStringExtra("boardImageUri");

        titleText.setText(correctionTitle); contentText.setText(correctionContent);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://dm951125.dothome.co.kr/"+correctionUri);
                    // Web에서 이미지를 가져온 뒤
                    // ImageView에 지정할 Bitmap을 만든다
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // 서버로 부터 응답 수신
                    conn.connect();
                    InputStream is = conn.getInputStream(); // InputStream 값 가져오기
                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace(); } }
        };
        mThread.start(); // Thread 실행
        try {
            // 메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야한다
            // join()를 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리게 한다
            mThread.join();
            // 작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
            // UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지를 지정한다
            imageUpload.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        ShareWriteCorrectionActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLEY_REQUEST
                );
            }
        });
        btnMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(90); //-360~360
                bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),rotateMatrix,false);
                imageUpload.setImageBitmap(bitmap);
            }
        });
        writeCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String boardTitle = titleText.getText().toString();
                final String boardContent = contentText.getText().toString();
                String fileName = MainActivity.userID +"_"+format2.format(time) + ".jpeg";
                progressDialog = new ProgressDialog(ShareWriteCorrectionActivity.this);
                progressDialog.setTitle("업로드중");
                progressDialog.setMessage("잠시만 기다려 주세요...");
                progressDialog.show();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShareWriteCorrectionActivity.this);
                        builder.setMessage("수정 완료")
                                .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,int which){
                                        Intent intent = new Intent(ShareWriteCorrectionActivity.this, ShareContent.class);
                                        intent.putExtra("boardTitle",boardTitle);
                                        intent.putExtra("boardContent",boardContent);
                                        intent.putExtra("userName", MainActivity.userIDName);
                                        intent.putExtra("boardDate",boardDate);
                                        intent.putExtra("boardNumber",correctionNumber);
                                        intent.putExtra("boardImageUrl","upload/images/"+fileName);
                                        ShareWriteCorrectionActivity.this.startActivity(intent);
                                        finish();                                    }
                                }).setCancelable(false)
                                .create()
                                .show();

                       }};
                ShareCorrectionRequest shareCorrectionRequest= new ShareCorrectionRequest(MainActivity.userIDName, boardTitle, boardContent,boardDate,correctionNumber, bitmap , fileName,correctionUri , responseListener);
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                queue.add(shareCorrectionRequest);
            }
        });



    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode ==CODE_GALLEY_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selec Image"), CODE_GALLEY_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"앨범 접근 권환을 허가하지 않았습니다.",Toast.LENGTH_LONG).show();

            }return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==CODE_GALLEY_REQUEST && resultCode ==RESULT_OK && data != null){
            Uri filePath = data.getData();
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90); //-360~360
            bitmap = resize(this,filePath,512);
            if(bitmap.getWidth()>=bitmap.getHeight()){
                bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),rotateMatrix,false);
                imageUpload.setImageBitmap(bitmap);
            }else{imageUpload.setImageBitmap(Bitmap.createBitmap(bitmap));}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }
            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }




}
