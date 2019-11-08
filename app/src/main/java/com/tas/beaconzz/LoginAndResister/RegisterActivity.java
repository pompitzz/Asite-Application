package com.tas.beaconzz.LoginAndResister;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.JsonPostRequest;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private ArrayAdapter adapter;
    private Spinner spinner;

    private AlertDialog dialog;
    private boolean validate = false; //회원아이디 체크 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = (Spinner) findViewById(R.id.majorSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final Spinner majorSpinner = (Spinner) findViewById(R.id.majorSpinner);
        final Button validateButton = (Button) findViewById(R.id.validateButton);

        validateButton.setOnClickListener(v -> {
            String userID = idText.getText().toString();
            if (validate) {
                return;
            }
            if (userID.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("아이디를 입력해 주세요.")
                        .setPositiveButton("확인", null)
                        .create();
                dialog.show();
                return;
            }
            //중복체크하는것
            //해당 웹사이트에 접속한 후에 응답을 받는것..
            Response.Listener<JSONObject> responseListener = jsonObject -> {
                try {
                    boolean success = jsonObject.getBoolean("success");//해당 과정이 정상적으로 수행되었는지에 대한 응답
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        idText.setEnabled(false); //id값 수정 불가하게 고정
                        validate = true; // 체크 완료
                        idText.setBackground(getResources().getDrawable(R.drawable.btn_gray));
                        validateButton.setBackground(getResources().getDrawable(R.drawable.btn_gray));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("중복된 아이디입니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        System.out.println("테스트1");
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            JSONObject jsonObject = VaildateRequestDto.createJson(userID);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this); //Request를 실질적으로 보내는 Queue
            queue.add(new JsonPostRequest("/student/join/validate", jsonObject, responseListener));
        });
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            String userID = idText.getText().toString();
            String userPassword = passwordText.getText().toString();
            String userName = nameText.getText().toString();
            String userEmail = emailText.getText().toString();
            String userMajor = majorSpinner.getSelectedItem().toString();
            if (!validate) { //중복체크가 되어있지 않을때
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                        .setNegativeButton("확인", null)
                        .create();
                dialog.show();
                return;
            }
            //빈 공간이 있을때
            if (userID.equals("") || userPassword.equals("") || userMajor.equals("전공을 선택해주세요.") || userName.equals("") || userEmail.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("빈 칸 없이 입력해주세요.")
                        .setNegativeButton("확인", null)
                        .create();
                dialog.show();
                return;
            }
            Response.Listener<JSONObject> responseListener = jsonObject -> {
                try {
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("회원등록에 성공했습니다")
                                .setPositiveButton("확인", (dialogInterface, which) -> {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    RegisterActivity.this.startActivity(intent);
                                    finish();
                                }).create()
                                .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("회원등록에 실패했습니다.")
                                .setNegativeButton("다시 시도", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            JSONObject jsonObject = ResisterRequestDto.createJson(userID, userPassword, userName, userEmail, userMajor);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(new JsonPostRequest("/student/join", jsonObject, responseListener));
        });


    }

    //회원등록이 완성되고 창이 꺼질때 발생하는 것
    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
