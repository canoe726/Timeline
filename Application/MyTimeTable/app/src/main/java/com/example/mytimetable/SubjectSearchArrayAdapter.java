package com.example.mytimetable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubjectSearchArrayAdapter extends BaseAdapter {

    private static String TAG = "php_SubjectSearchArrayAdapter";

    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSCODE = "class_code";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CLASS_DAY = "class_day";
    private static final String TAG_CLASS_TIME = "class_time";
    private static final String TAG_CLASS_SCHEDULE = "class_schedule";

    private String global_user_id;
    private String mJsonString;

    private ArrayList<HashMap<String, String>> search_list;
    private Context context;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    private Schedule schedule = new Schedule();
    private List<String> class_code_list;
    private List<String> user_schedule_list;

    public SubjectSearchArrayAdapter(ArrayList<HashMap<String, String>> search_list, String user_id, Context context) {
        this.search_list = search_list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        this.global_user_id = user_id;

        schedule = new Schedule();
        class_code_list = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return search_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;

        convertView = inflate.inflate(R.layout.activity_subject_search_list_item,null);

        viewHolder = new ViewHolder();
        viewHolder.class_name = (TextView) convertView.findViewById(R.id.class_name);
        viewHolder.class_code = (TextView) convertView.findViewById(R.id.class_code);
        viewHolder.professor = (TextView) convertView.findViewById(R.id.professor);
        viewHolder.classroom = (TextView) convertView.findViewById(R.id.classroom);
        viewHolder.class_day = (TextView) convertView.findViewById(R.id.class_day);
        viewHolder.class_time = (TextView) convertView.findViewById(R.id.class_time);
        viewHolder.subject_add_button = (Button) convertView.findViewById(R.id.subject_add_button);
        viewHolder.subject_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = GetScheduleFromDB(global_user_id);
                if (res != null) {
                    mJsonString = res;
                    user_schedule_list = new ArrayList<String>();

                    try {
                        JSONObject jsonObject = new JSONObject(mJsonString);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject object = jsonArray.getJSONObject(count);

                            String class_code = object.getString("class_code");
                            String class_schedule = object.getString("class_schedule");
                            user_schedule_list.add(class_schedule);

                            class_code_list.add(class_code);
                            schedule.addSchedule(class_schedule);
                            count++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!alreadyIn(class_code_list, search_list.get(pos).get(TAG_CLASSCODE))) { // 이미 등록한 강의일 경우
                        Toast.makeText(context, "이미 추가한 강의입니다.", Toast.LENGTH_SHORT).show();
                    } else {

                        boolean validate = false;
                        validate = schedule.validate(search_list.get(pos).get(TAG_CLASS_SCHEDULE));

                        if (validate == false) { // 중복된 강의일 경우

                            Toast.makeText(context, "시간표가 중복됩니다.", Toast.LENGTH_SHORT).show();

                        } else {

                            class_code_list.add(search_list.get(pos).get(TAG_CLASSCODE));
                            schedule.addSchedule(search_list.get(pos).get(TAG_CLASSCODE));

                            schedule.addSchedule(search_list.get(pos).get(TAG_CLASS_SCHEDULE), search_list.get(pos).get(TAG_CLASSNAME), search_list.get(pos).get(TAG_PROFESSOR));

                            SendScheduleToDB(global_user_id, search_list.get(pos).get(TAG_CLASSCODE));

                            String texts = search_list.get(pos).get(TAG_CLASSNAME) + " 과목을 등록했습니다.";
                            Toast.makeText(context, texts, Toast.LENGTH_SHORT).show();

                            ((MainActivity)MainActivity.MAIN_CONTEXT).onRestart();
                        }
                    }
                } else {
                    Toast.makeText(context, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertView.setTag(viewHolder);

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.class_name.setText(search_list.get(position).get(TAG_CLASSNAME));
        viewHolder.class_code.setText(search_list.get(position).get(TAG_CLASSCODE));
        viewHolder.professor.setText(search_list.get(position).get(TAG_PROFESSOR));
        viewHolder.classroom.setText(search_list.get(position).get(TAG_CLASSROOM));
        viewHolder.class_day.setText(search_list.get(position).get(TAG_CLASS_DAY));
        viewHolder.class_time.setText(search_list.get(position).get(TAG_CLASS_TIME));

        return convertView;
    }

    class ViewHolder{
        public TextView class_name;
        public TextView class_code;
        public TextView professor;
        public TextView classroom;
        public TextView class_day;
        public TextView class_time;
        public Button subject_add_button;
    }

    public List<String> parseSchedule(String user_schedule) {
        List<String> result = new ArrayList<String>();
        int start = 0, end = 0;
        for(int i=0; i<user_schedule.length(); i++) {
            if( user_schedule.charAt(i) == ',' ) {
                end = i;
                result.add(user_schedule.substring(start, end));
                start = i + 1;
            }
        }
        return result;
    }

    public boolean alreadyIn(List<String> class_code_list, String class_code) {

        for(int i=0; i<class_code_list.size(); i++) {
            if( class_code_list.get(i).equals(class_code) ) {
                return false;
            }
        }
        return true;
    }

    private String GetScheduleFromDB(String user_id) {
        class GetData extends AsyncTask<String, Void, String> {
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.53/Timeline/load_user_schedule_info.php";

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

    private String SendScheduleToDB(String user_id, String class_code) {
        class GetData extends AsyncTask<String, Void, String> {
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.53/Timeline/upload_user_subject_info.php";

                    String userID = (String) params[0];
                    String classCODE = (String) params[1];

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("classCODE", "UTF-8") + "=" + URLEncoder.encode(classCODE, "UTF-8");

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
                    return "CLEAR";

                } catch (Exception e) {
                    errorString = e.toString();
                    return null;
                }
            }
        }
        GetData task = new GetData();

        try {
            return task.execute(user_id, class_code).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
