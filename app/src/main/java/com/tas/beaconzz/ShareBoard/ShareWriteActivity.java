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
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Board.BoardRequest;
import com.tas.beaconzz.Board.WriteActivity;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShareWriteActivity extends AppCompatActivity {
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
        btnMatrix = (Button) findViewById(R.id.btnMatrix);

        imageUpload = (ImageView) findViewById(R.id.imageUpload);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        Button writeCompletion = (Button) findViewById(R.id.writeCompletion);
        final EditText titleText = (EditText) findViewById(R.id.titleText);
        final EditText contentText = (EditText) findViewById(R.id.contentText);
        final String boardDate = format1.format(time);

        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        ShareWriteActivity.this,
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
                progressDialog = new ProgressDialog(ShareWriteActivity.this);
                progressDialog.setTitle("업로드중");
                progressDialog.setMessage("잠시만 기다려 주세요...");
                progressDialog.show();
                String boardTitle = titleText.getText().toString();
                String boardContent = contentText.getText().toString();
                String fileName = MainActivity.userID +"_"+format2.format(time) + ".jpeg";
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShareWriteActivity.this);
                        builder.setMessage("글쓰기 완료")
                                .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,int which){
                                        finish();
                                    }
                                }).setCancelable(false)
                                .create()
                                .show();
                    }};
                ShareRequest shareRequest= new ShareRequest(MainActivity.userIDName, boardTitle, boardContent,boardDate, bitmap, fileName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                queue.add(shareRequest);
            }
        });


    }@Override
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
