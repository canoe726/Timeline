package com.example.mytimetable;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SubjectListActivity extends AppCompatActivity {

    private String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSCODE = "class_code";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CLASS_DAY = "class_day";
    private static final String TAG_CLASS_TIME = "class_time";
    private static final String TAG_CLASS_INFO = "information";

    GlobalVariables globalVariables;
    private String global_user_id;

    private JSONArray subjects = null;

    private ArrayList<HashMap<String, String>> subject_list;

    private ListView listView;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        listView = (ListView)findViewById(R.id.subject_listview);
        subject_list = new ArrayList<HashMap<String, String>>();

        globalVariables = (GlobalVariables) getApplication();
        global_user_id = globalVariables.getGlobalUserId();

        getData("http://123.109.137.53/Timeline/load_user_subject_list.php");
    }

    protected void showList() {
        try {

            JSONObject jsonObject = new JSONObject(myJSON);
            subjects = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i=0; i<subjects.length(); i++) {
                JSONObject object = subjects.getJSONObject(i);
                String class_name = object.getString(TAG_CLASSNAME);
                String class_code = object.getString(TAG_CLASSCODE);
                String professor = object.getString(TAG_PROFESSOR);
                String classroom = object.getString(TAG_CLASSROOM);
                String class_day = object.getString(TAG_CLASS_DAY);
                String class_time = object.getString(TAG_CLASS_TIME);
                String class_info = object.getString(TAG_CLASS_INFO);

                HashMap<String, String> subject_item = new HashMap<String, String>();
                subject_item.put(TAG_CLASSNAME, class_name);
                subject_item.put(TAG_CLASSCODE, class_code);
                subject_item.put(TAG_PROFESSOR, professor);;
                subject_item.put(TAG_CLASSROOM, classroom);
                subject_item.put(TAG_CLASS_DAY, class_day);
                subject_item.put(TAG_CLASS_TIME, class_time);
                subject_item.put(TAG_CLASS_INFO, class_info);

                subject_list.add(subject_item);
            }

            adapter = new SubjectListArrayAdapter(subject_list, globalVariables.getGlobalUserId(),this);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(final String server_url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(SubjectListActivity.this,
                        "잠시만 기다려주세요", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                myJSON = result;
                showList();
            }

            @Override
            protected String doInBackground(String... params) {
                String s_url = server_url;
                try {
                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(global_user_id, "UTF-8");

                    URL url = new URL(s_url);

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
                    Log.d(TAG_RESULTS, "response code - " + responseStatusCode);

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

        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(server_url);
    }
}
