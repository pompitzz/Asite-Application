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
        EditText idText = (EditText) findViewById(R.id.idText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        /**
         * 회원가입 화면으로 이동
         */
        registerButton.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(registerIntent);
        });


        /**
         * 로그인 기능
         */
        loginButton.setOnClickListener(v -> {

            String userID = idText.getText().toString();
            String userPassword = passwordText.getText().toString();

            Response.Listener<JSONObject> responseLister = jsonObject -> {
                try {

                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        Long user_Key = jsonObject.getLong("studentId");
                        String name = jsonObject.getString("name");

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 성공했습니다.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("studentId", user_Key);
                        intent.putExtra("name", name);
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


