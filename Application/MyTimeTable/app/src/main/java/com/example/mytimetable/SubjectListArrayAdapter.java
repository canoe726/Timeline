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

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SubjectListArrayAdapter extends BaseAdapter {

    private static String TAG = "php_SubjectListArrayAdapter";

    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSCODE = "class_code";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CLASS_DAY = "class_day";
    private static final String TAG_CLASS_TIME = "class_time";

    private String global_user_id;

    private ArrayList<HashMap<String, String>> subject_list;
    private Context context;
    private LayoutInflater inflate;
    private SubjectListArrayAdapter.ViewHolder viewHolder;

    public SubjectListArrayAdapter(ArrayList<HashMap<String, String>> subject_list, String user_id, Context context) {
        this.subject_list = subject_list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        this.global_user_id = user_id;
    }


    @Override
    public int getCount() {
        return subject_list.size();
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

        convertView = inflate.inflate(R.layout.subject_lists_item,null);

        viewHolder = new ViewHolder();
        viewHolder.class_name = (TextView) convertView.findViewById(R.id.class_name);
        viewHolder.class_code = (TextView) convertView.findViewById(R.id.class_code);
        viewHolder.professor = (TextView) convertView.findViewById(R.id.professor);
        viewHolder.classroom = (TextView) convertView.findViewById(R.id.classroom);
        viewHolder.class_day = (TextView) convertView.findViewById(R.id.class_day);
        viewHolder.class_time = (TextView) convertView.findViewById(R.id.class_time);
        viewHolder.class_delete_button = (Button) convertView.findViewById(R.id.class_delete_button);
        viewHolder.class_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteScheduleFromDB(global_user_id, subject_list.get(pos).get(TAG_CLASSCODE));
                Toast.makeText(context, "해당 과목을 삭제했습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        convertView.setTag(viewHolder);

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.class_name.setText(subject_list.get(position).get(TAG_CLASSNAME));
        viewHolder.class_code.setText(subject_list.get(position).get(TAG_CLASSCODE));
        viewHolder.professor.setText(subject_list.get(position).get(TAG_PROFESSOR));
        viewHolder.classroom.setText(subject_list.get(position).get(TAG_CLASSROOM));
        viewHolder.class_day.setText(subject_list.get(position).get(TAG_CLASS_DAY));
        viewHolder.class_time.setText(subject_list.get(position).get(TAG_CLASS_TIME));

        return convertView;
    }

    class ViewHolder{
        public TextView class_name;
        public TextView class_code;
        public TextView professor;
        public TextView classroom;
        public TextView class_day;
        public TextView class_time;
        public Button class_delete_button;
    }

    private String DeleteScheduleFromDB(String user_id, String class_code) {
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
                    String serverURL = "http://123.109.137.53/Timeline/delete_user_schedule.php";

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
