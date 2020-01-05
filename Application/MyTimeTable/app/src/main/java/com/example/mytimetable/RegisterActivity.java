package com.example.mytimetable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    private static String TAG = "php_RegisterActivity";

    private Intent intent;

    private String user_id;
    private String user_pw;
    private String user_email;

    private AlertDialog dialog;

    private EditText idText;
    private EditText pwText;
    private EditText emailText;

    private Button validateButton;
    private Button registerButton;

    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = (EditText) findViewById(R.id.idText);
        pwText = (EditText) findViewById(R.id.pwText);
        emailText = (EditText) findViewById(R.id.emailText);

        validateButton = (Button) findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = idText.getText().toString();

                if(validate) {
                    return;
                }

                if(user_id.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                String info = CheckUserIdFromDB(user_id);
                String valid_id = "";
                try {
                    JSONObject jsonObject = new JSONObject(info);
                    valid_id = jsonObject.getString("user_id");
                } catch (
                JSONException e) {
                    e.printStackTrace();
                }

                if(valid_id.equals("null")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    idText.setEnabled(false);
                    validate = true;
                    idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이미 존재하는 아이디입니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                }
            }
        });

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = idText.getText().toString();
                user_pw = pwText.getText().toString();
                user_email = emailText.getText().toString();

                if(!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(user_id.equals("") || user_pw.equals("") || user_email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력 해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                InsertUserInfoToDB(user_id, user_pw, user_email);

                Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent); // 다음화면으로 넘어가기
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void InsertUserInfoToDB(String user_id, String user_pw, String user_email) {
        class GetData extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterActivity.this,
                        "잠시만 기다려주세요", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.53/Timeline/register_user_info.php";

                    String userID = (String) params[0];
                    String userPW = (String) params[1];
                    String userEMAIL = (String) params[2];

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("userPW", "UTF-8") + "=" + URLEncoder.encode(userPW, "UTF-8");
                    data += "&" + URLEncoder.encode("userEMAIL", "UTF-8") + "=" + URLEncoder.encode(userEMAIL, "UTF-8");

                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }
                    return null;

                } catch (Exception e) {
                    errorString = e.toString();
                    return null;
                }
            }
        }
        GetData task = new GetData();
        task.execute(user_id, user_pw, user_email);
    }

    private String CheckUserIdFromDB(String user_id) {
        class GetData extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterActivity.this,
                        "잠시만 기다려주세요", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.53/Timeline/validate_user_info.php";

                    String userID = (String) params[0];

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");

                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    return sb.toString().trim();

                } catch (Exception e) {
                    errorString = e.toString();
                    return null;
                }
            }
        }
        GetData task = new GetData();
        try {
            return task.execute(user_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
