package com.example.mytimetable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private Intent intent;

    private TextView current_url_textview;
    private TextView user_id_textview;

    private EditText url_edittext;

    private Button logout_button;
    private Button url_save_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        current_url_textview = (TextView)findViewById(R.id.current_url_textview);
        user_id_textview = (TextView)findViewById(R.id.user_id_textview);

        url_edittext = (EditText)findViewById(R.id.url_edittext);

        logout_button = (Button)findViewById(R.id.logout_button);
        url_save_button = (Button)findViewById(R.id.url_save_button);

        GlobalVariables myApp = (GlobalVariables) getApplication();
        String global_user_id = "";
        global_user_id = myApp.getGlobalUserId();
        user_id_textview.setText(global_user_id);

        String global_url = "";
        global_url = myApp.getGlobalUniversityUrl();
        current_url_textview.setText(global_url);

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();

                Toast.makeText(getApplicationContext(), "로그인 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent); // 다음화면으로 넘어가기

                finish();
            }
        });

        url_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_url = url_edittext.getText().toString();
                input_url = "http://" + input_url;

                GlobalVariables myApp = (GlobalVariables) getApplication();
                myApp.setGlobalUniversityUrl(input_url);

                Toast.makeText(getApplicationContext(), "URL이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
