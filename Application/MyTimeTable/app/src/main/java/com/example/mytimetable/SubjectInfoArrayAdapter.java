package com.example.mytimetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectInfoArrayAdapter extends BaseAdapter {

    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSCODE = "class_code";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CLASS_DAY = "class_day";
    private static final String TAG_CLASS_TIME = "class_time";
    private static final String TAG_CLASS_INFO = "information";

    private String global_user_id;

    private ArrayList<HashMap<String, String>> subject_list;
    private Context context;
    private LayoutInflater inflate;
    private SubjectInfoArrayAdapter.ViewHolder viewHolder;

    public SubjectInfoArrayAdapter(ArrayList<HashMap<String, String>> subject_list, String user_id, Context context) {
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

        convertView = inflate.inflate(R.layout.subject_info_lists_item,null);

        viewHolder = new ViewHolder();
        viewHolder.class_name = (TextView) convertView.findViewById(R.id.class_name);
        viewHolder.class_code = (TextView) convertView.findViewById(R.id.class_code);
        viewHolder.professor = (TextView) convertView.findViewById(R.id.professor);
        viewHolder.classroom = (TextView) convertView.findViewById(R.id.classroom);
        viewHolder.class_day = (TextView) convertView.findViewById(R.id.class_day);
        viewHolder.class_time = (TextView) convertView.findViewById(R.id.class_time);
        viewHolder.class_info = (TextView) convertView.findViewById(R.id.class_info);

        convertView.setTag(viewHolder);

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.class_name.setText(subject_list.get(position).get(TAG_CLASSNAME));
        viewHolder.class_code.setText(subject_list.get(position).get(TAG_CLASSCODE));
        viewHolder.professor.setText(subject_list.get(position).get(TAG_PROFESSOR));
        viewHolder.classroom.setText(subject_list.get(position).get(TAG_CLASSROOM));
        viewHolder.class_day.setText(subject_list.get(position).get(TAG_CLASS_DAY));
        viewHolder.class_time.setText(subject_list.get(position).get(TAG_CLASS_TIME));
        viewHolder.class_info.setText(subject_list.get(position).get(TAG_CLASS_INFO));

        return convertView;
    }

    class ViewHolder{
        public TextView class_name;
        public TextView class_code;
        public TextView professor;
        public TextView classroom;
        public TextView class_day;
        public TextView class_time;
        public TextView class_info;
    }
}
