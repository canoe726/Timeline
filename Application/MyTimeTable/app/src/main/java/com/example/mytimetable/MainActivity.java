package com.example.mytimetable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static Context MAIN_CONTEXT;

    private static String TAG = "php_MainActivity";

    private TextView yyyy_mm_textview;

    private TextView monday_textview;
    private TextView tuesday_textview;
    private TextView wednesday_textview;
    private TextView thursday_textview;
    private TextView friday_textview;

    private TextView mon_date_textview;
    private TextView tue_date_textview;
    private TextView wed_date_textview;
    private TextView thu_date_textview;
    private TextView fri_date_textview;

    private Button last_week_button;
    private Button cur_week_button;
    private Button next_week_button;

    private Button university_home_button;
    private Button subject_list_button;
    private Button setting_button;

    public TextView[][] textView_table = new TextView[5][12];

    private SimpleDateFormat simpleDateFormat;
    private Date currentTime;

    GlobalVariables globalVariables;
    private String global_user_id;
    private String mJsonString;
    private List<String> class_name_list;

    public Schedule schedule = new Schedule();

    private int i, j;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize all variables
        init();

        /*
        // 숫자를 오늘의 날짜로 변경
        int[] date = new int [7];

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        int today_num = cal.get(cal.DAY_OF_WEEK);

        int index = today_num;
        for(int i=1; i<=6; i++) {
            cal.add(Calendar.DATE, i);

            int dayNum = cal.get(cal.DAY_OF_WEEK);
            date[dayNum] = cal.get(cal.DATE);
        }

        mon_date_textview.setText(date[2]);
        tue_date_textview.setText(date[3]);
        wed_date_textview.setText(date[4]);
        thu_date_textview.setText(date[5]);
        fri_date_textview.setText(date[6]);
        */

        // 오늘의 날짜는 파란색으로 변경
        simpleDateFormat = new SimpleDateFormat("EE");
        String today = simpleDateFormat.format(currentTime);
        String strColor = "#0064FF";

        if( today.equals("월")) {
            monday_textview.setTextColor(Color.parseColor(strColor));
            mon_date_textview.setTextColor(Color.parseColor(strColor));
        } else if( today.equals("화")) {
            tuesday_textview.setTextColor(Color.parseColor(strColor));
            tue_date_textview.setTextColor(Color.parseColor(strColor));
        } else if( today.equals("수")) {
            wednesday_textview.setTextColor(Color.parseColor(strColor));
            wed_date_textview.setTextColor(Color.parseColor(strColor));
        } else if( today.equals("목")) {
            thursday_textview.setTextColor(Color.parseColor(strColor));
            thu_date_textview.setTextColor(Color.parseColor(strColor));
        } else if( today.equals("금")) {
            friday_textview.setTextColor(Color.parseColor(strColor));
            fri_date_textview.setTextColor(Color.parseColor(strColor));
        }

        // 테이블로 시간표 불러오기
        String res = GetScheduleFromDB(global_user_id);
        if (res != null) {
            mJsonString = res;
            class_name_list = new ArrayList<String>();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    String class_schedule = object.getString("class_schedule");
                    String class_name = object.getString("class_name");
                    String professor = object.getString("professor");

                    class_name_list.add(class_name);
                    schedule.addSchedule(class_schedule, class_name, professor);
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            schedule.changeColorInTable(class_name_list, textView_table);
        } else {
            Toast.makeText(this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
        }

        // 테이블 행 클릭시 이벤트
        for(i=0; i<5; i++) {
            for(j=0; j<12; j++) {
                ColorDrawable colorDrawable = (ColorDrawable) textView_table[i][j].getBackground();
                final String putdata = textView_table[i][j].getText().toString();
                final int colorCode = colorDrawable.getColor();

                textView_table[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 시간표가 채워져 있지 않는 경우
                        if( colorCode == -1 ) {
                            intent = new Intent(getApplicationContext(), SearchPopupActivity.class);
                            intent.putExtra("data", "Search");
                            startActivity(intent);
                        } else { // 시간표가 채워진 경우
                            intent = new Intent(getApplicationContext(), InfoPopupActivity.class);
                            intent.putExtra("Subject_name", putdata);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        // 대학교 홈 버튼
        university_home_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_link;
                url_link = globalVariables.getGlobalUniversityUrl();

                if( url_link.equals("")) {
                    Toast.makeText(getApplicationContext(), "설정에서 URL을 등록하세요", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_link));
                    startActivity(intent);
                }
            }
        });

        // 과목 목록 버튼
        subject_list_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubjectListActivity.class);
                startActivity(intent);
            }
        });

        // 설정 버튼
        setting_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    public String getDateDay(String date, String dateType) throws Exception {
        String day = "" ;

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType) ;
        Date nDate = dateFormat.parse(date) ;

        Calendar cal = Calendar.getInstance() ;
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;

        switch(dayNum){
            case 1:
                day = "일";
                break ;
            case 2:
                day = "월";
                break ;
            case 3:
                day = "화";
                break ;
            case 4:
                day = "수";
                break ;
            case 5:
                day = "목";
                break ;
            case 6:
                day = "금";
                break ;
            case 7:
                day = "토";
                break ;
        }

        return day ;
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

    public void init_tableView(TextView[][] textView_table) {
        textView_table[0][0]  = (TextView)findViewById(R.id.Mon_col_0);
        textView_table[0][1]  = (TextView)findViewById(R.id.Mon_col_1);
        textView_table[0][2]  = (TextView)findViewById(R.id.Mon_col_2);
        textView_table[0][3]  = (TextView)findViewById(R.id.Mon_col_3);
        textView_table[0][4]  = (TextView)findViewById(R.id.Mon_col_4);
        textView_table[0][5]  = (TextView)findViewById(R.id.Mon_col_5);
        textView_table[0][6]  = (TextView)findViewById(R.id.Mon_col_6);
        textView_table[0][7]  = (TextView)findViewById(R.id.Mon_col_7);
        textView_table[0][8]  = (TextView)findViewById(R.id.Mon_col_8);
        textView_table[0][9]  = (TextView)findViewById(R.id.Mon_col_9);
        textView_table[0][10] = (TextView)findViewById(R.id.Mon_col_10);
        textView_table[0][11] = (TextView)findViewById(R.id.Mon_col_11);

        textView_table[1][0]  = (TextView)findViewById(R.id.Tue_col_0);
        textView_table[1][1]  = (TextView)findViewById(R.id.Tue_col_1);
        textView_table[1][2]  = (TextView)findViewById(R.id.Tue_col_2);
        textView_table[1][3]  = (TextView)findViewById(R.id.Tue_col_3);
        textView_table[1][4]  = (TextView)findViewById(R.id.Tue_col_4);
        textView_table[1][5]  = (TextView)findViewById(R.id.Tue_col_5);
        textView_table[1][6]  = (TextView)findViewById(R.id.Tue_col_6);
        textView_table[1][7]  = (TextView)findViewById(R.id.Tue_col_7);
        textView_table[1][8]  = (TextView)findViewById(R.id.Tue_col_8);
        textView_table[1][9]  = (TextView)findViewById(R.id.Tue_col_9);
        textView_table[1][10] = (TextView)findViewById(R.id.Tue_col_10);
        textView_table[1][11] = (TextView)findViewById(R.id.Tue_col_11);

        textView_table[2][0]  = (TextView)findViewById(R.id.Wed_col_0);
        textView_table[2][1]  = (TextView)findViewById(R.id.Wed_col_1);
        textView_table[2][2]  = (TextView)findViewById(R.id.Wed_col_2);
        textView_table[2][3]  = (TextView)findViewById(R.id.Wed_col_3);
        textView_table[2][4]  = (TextView)findViewById(R.id.Wed_col_4);
        textView_table[2][5]  = (TextView)findViewById(R.id.Wed_col_5);
        textView_table[2][6]  = (TextView)findViewById(R.id.Wed_col_6);
        textView_table[2][7]  = (TextView)findViewById(R.id.Wed_col_7);
        textView_table[2][8]  = (TextView)findViewById(R.id.Wed_col_8);
        textView_table[2][9]  = (TextView)findViewById(R.id.Wed_col_9);
        textView_table[2][10] = (TextView)findViewById(R.id.Wed_col_10);
        textView_table[2][11] = (TextView)findViewById(R.id.Wed_col_11);

        textView_table[3][0]  = (TextView)findViewById(R.id.Thu_col_0);
        textView_table[3][1]  = (TextView)findViewById(R.id.Thu_col_1);
        textView_table[3][2]  = (TextView)findViewById(R.id.Thu_col_2);
        textView_table[3][3]  = (TextView)findViewById(R.id.Thu_col_3);
        textView_table[3][4]  = (TextView)findViewById(R.id.Thu_col_4);
        textView_table[3][5]  = (TextView)findViewById(R.id.Thu_col_5);
        textView_table[3][6]  = (TextView)findViewById(R.id.Thu_col_6);
        textView_table[3][7]  = (TextView)findViewById(R.id.Thu_col_7);
        textView_table[3][8]  = (TextView)findViewById(R.id.Thu_col_8);
        textView_table[3][9]  = (TextView)findViewById(R.id.Thu_col_9);
        textView_table[3][10] = (TextView)findViewById(R.id.Thu_col_10);
        textView_table[3][11] = (TextView)findViewById(R.id.Thu_col_11);

        textView_table[4][0]  = (TextView)findViewById(R.id.Fri_col_0);
        textView_table[4][1]  = (TextView)findViewById(R.id.Fri_col_1);
        textView_table[4][2]  = (TextView)findViewById(R.id.Fri_col_2);
        textView_table[4][3]  = (TextView)findViewById(R.id.Fri_col_3);
        textView_table[4][4]  = (TextView)findViewById(R.id.Fri_col_4);
        textView_table[4][5]  = (TextView)findViewById(R.id.Fri_col_5);
        textView_table[4][6]  = (TextView)findViewById(R.id.Fri_col_6);
        textView_table[4][7]  = (TextView)findViewById(R.id.Fri_col_7);
        textView_table[4][8]  = (TextView)findViewById(R.id.Fri_col_8);
        textView_table[4][9]  = (TextView)findViewById(R.id.Fri_col_9);
        textView_table[4][10] = (TextView)findViewById(R.id.Fri_col_10);
        textView_table[4][11] = (TextView)findViewById(R.id.Fri_col_11);
    }

    public void init() {
        yyyy_mm_textview = (TextView) findViewById(R.id.yyyy_mm_textview);

        monday_textview = (TextView) findViewById(R.id.Monday_textview);
        tuesday_textview = (TextView) findViewById(R.id.Tuesday_textview);
        wednesday_textview = (TextView) findViewById(R.id.Wednesday_textview);
        thursday_textview = (TextView) findViewById(R.id.Thursday_textview);
        friday_textview = (TextView) findViewById(R.id.Friday_textview);

        mon_date_textview = (TextView) findViewById(R.id.Mon_date_textview);
        tue_date_textview = (TextView) findViewById(R.id.Tue_date_textview);
        wed_date_textview = (TextView) findViewById(R.id.Wed_date_textview);
        thu_date_textview = (TextView) findViewById(R.id.Thu_date_textview);
        fri_date_textview = (TextView) findViewById(R.id.Fri_date_textview);

        last_week_button = (Button) findViewById(R.id.last_week_button);
        cur_week_button = (Button) findViewById(R.id.cur_week_button);
        next_week_button = (Button) findViewById(R.id.next_week_button);

        university_home_button = (Button) findViewById(R.id.university_home_button);
        subject_list_button = (Button) findViewById(R.id.subject_list_button);
        setting_button = (Button) findViewById(R.id.setting_button);

        // 시간표의 년,월 출력
        currentTime = Calendar.getInstance().getTime();
        simpleDateFormat = new SimpleDateFormat("yyyy");
        String year = simpleDateFormat.format(currentTime);
        simpleDateFormat = new SimpleDateFormat("MM");
        String month = simpleDateFormat.format(currentTime);
        yyyy_mm_textview.setText(year + "년" + " " + month + "월");

        globalVariables = (GlobalVariables) getApplication();
        global_user_id = globalVariables.getGlobalUserId();
        schedule = new Schedule();
        MAIN_CONTEXT = this;

        init_tableView(textView_table);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 테이블 행 클릭시 이벤트
        for(i=0; i<5; i++) {
            for(j=0; j<12; j++) {
                ColorDrawable colorDrawable = (ColorDrawable) textView_table[i][j].getBackground();
                final String putdata = textView_table[i][j].getText().toString();
                final int colorCode = colorDrawable.getColor();

                textView_table[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 시간표가 채워져 있지 않는 경우
                        if( colorCode == -1 ) {
                            intent = new Intent(getApplicationContext(), SearchPopupActivity.class);
                            intent.putExtra("data", "Search");
                            startActivity(intent);
                        } else { // 시간표가 채워진 경우
                            intent = new Intent(getApplicationContext(), InfoPopupActivity.class);
                            intent.putExtra("Subject_name", putdata);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 테이블 초기화
        for(i=0; i<5; i++) {
            for(j=0; j<12; j++) {
                textView_table[i][j].setBackgroundColor(Color.parseColor("#ffffff"));
                textView_table[i][j].setText("");
            }
        }

        // 테이블로 시간표 불러오기
        String res = GetScheduleFromDB(global_user_id);
        if (res != null) {
            mJsonString = res;
            class_name_list = new ArrayList<String>();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    String class_schedule = object.getString("class_schedule");
                    String class_name = object.getString("class_name");
                    String professor = object.getString("professor");

                    class_name_list.add(class_name);
                    schedule.addSchedule(class_schedule, class_name, professor);
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            schedule.changeColorInTable(class_name_list, textView_table);
        } else {
            Toast.makeText(this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
