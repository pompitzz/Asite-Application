package com.tas.beaconzz.LoginAndResister;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.JsonPostRequest;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView registerButton = (TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(registerIntent);

        });


        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            final String userID = idText.getText().toString();
            final String userPassword = passwordText.getText().toString();

            //결과를 받기 위한 것
            Response.Listener<JSONObject> responseLister = jsonObject -> {
                try {
                    boolean success = jsonObject.getBoolean("success"); // boolean 값으로 성공을 의미하는 값을 지정
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 성공했습니다.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userID", userID); // userID 정보보내기 위에 final 붙여야함(전달할때 사용 할 이름, 전달할 값)
                        intent.putExtra("userPassword", userPassword);
                        LoginActivity.this.startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        dialog = builder.setMessage("계정을 다시 확인하세요.")
                                .setNegativeButton("다시 시도", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            JSONObject jsonObject = LoginRequestDto.createJson(userID, userPassword);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(new JsonPostRequest("/student/login", jsonObject, responseLister));
        });
    }

}


