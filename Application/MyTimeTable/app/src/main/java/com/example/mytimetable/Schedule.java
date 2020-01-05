package com.example.mytimetable;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Schedule {

    private String days[][] = new String[5][12];
    private List<String> colors = new ArrayList<String>();

    public Schedule() {
        for(int i=0; i<5; i++) {
            for(int j=0; j<12; j++) {
                days[i][j] = "";
            }
        }

        initColors();
    }

    public void addSchedule(String scheduleText) {
        // (월,화)[0-2]
        int find_days[] = new int[5];
        for(int i=0; i<5; i++) {
            find_days[i] = 0;
        }

        int index = 1;
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) == ')' ) {
                index = i + 2;
                break;
            } else {
                if( scheduleText.charAt(i) == '월' ) {
                    find_days[0] += 1;
                } else if( scheduleText.charAt(i) == '화' ) {
                    find_days[1] += 1;
                } else if( scheduleText.charAt(i) == '수' ) {
                    find_days[2] += 1;
                } else if( scheduleText.charAt(i) == '목' ) {
                    find_days[3] += 1;
                } else if( scheduleText.charAt(i) == '금' ) {
                    find_days[4] += 1;
                }
            }
        }

        int class_time[] = new int[2];
        int class_time_index = 0;
        String time = "";
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) >= '0' && scheduleText.charAt(i) <= '9' ) {
                time = time + scheduleText.charAt(i);
            } else {
                class_time[class_time_index] = Integer.parseInt(time);
                time = "";
                class_time_index += 1;
            }
        }

        for(int i=0; i<5; i++) {
            for(int j=class_time[0]; j<class_time[1]; j++) {
                if( find_days[i] == 1 ) {
                    days[i][j] = "수업";
                }
            }
        }
    }

    public boolean validate(String scheduleText) {

        // (월,화)[0-2]
        int find_days[] = new int[5];
        for(int i=0; i<5; i++) {
            find_days[i] = 0;
        }

        int index = 1;
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) == ')' ) {
                index = i + 2;
                break;
            } else {
                if( scheduleText.charAt(i) == '월' ) {
                    find_days[0] += 1;
                } else if( scheduleText.charAt(i) == '화' ) {
                    find_days[1] += 1;
                } else if( scheduleText.charAt(i) == '수' ) {
                    find_days[2] += 1;
                } else if( scheduleText.charAt(i) == '목' ) {
                    find_days[3] += 1;
                } else if( scheduleText.charAt(i) == '금' ) {
                    find_days[4] += 1;
                }
            }
        }

        int class_time[] = new int[2];
        int class_time_index = 0;
        String time = "";
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) >= '0' && scheduleText.charAt(i) <= '9' ) {
                time = time + scheduleText.charAt(i);
            } else {
                class_time[class_time_index] = Integer.parseInt(time);
                time = "";
                class_time_index += 1;
            }
        }

        for(int i=0; i<5; i++) {
            if(find_days[i] == 1) {
                for (int j = class_time[0]; j < class_time[1]; j++) {
                    if (!days[i][j].equals("")) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void addSchedule(String scheduleText, String class_name, String course_professor) {

        String professor;
        if(course_professor.equals("")) {
            professor = "";
        } else {
            professor = "[" + course_professor + "]";
        }

        // (월,화)[0-2]
        int find_days[] = new int[5];
        for(int i=0; i<5; i++) {
            find_days[i] = 0;
        }

        int index = 1;
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) == ')' ) {
                index = i + 2;
                break;
            } else {
                if( scheduleText.charAt(i) == '월' ) {
                    find_days[0] += 1;
                } else if( scheduleText.charAt(i) == '화' ) {
                    find_days[1] += 1;
                } else if( scheduleText.charAt(i) == '수' ) {
                    find_days[2] += 1;
                } else if( scheduleText.charAt(i) == '목' ) {
                    find_days[3] += 1;
                } else if( scheduleText.charAt(i) == '금' ) {
                    find_days[4] += 1;
                }
            }
        }

        int class_time[] = new int[2];
        int class_time_index = 0;
        String time = "";
        for(int i=index; i<scheduleText.length(); i++) {
            if( scheduleText.charAt(i) >= '0' && scheduleText.charAt(i) <= '9' ) {
                time = time + scheduleText.charAt(i);
            } else {
                class_time[class_time_index] = Integer.parseInt(time);
                time = "";
                class_time_index += 1;
            }
        }

        for(int i=0; i<5; i++) {
            for(int j=class_time[0]; j<class_time[1]; j++) {
                if( find_days[i] == 1 ) {
                    days[i][j] = class_name + '\n' + professor;
                }
            }
        }
    }

    public void changeColorInTable(List<String> class_code_list, TextView[][] textView_table) {

        List<Integer> color_index = new ArrayList<Integer>();

        for(int i=0; i<class_code_list.size(); i++) {
            Random random = new Random();
            int rand_idx = random.nextInt(colors.size());
            color_index.add(rand_idx);
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (!days[i][j].equals("")) {
                    for (int k = 0; k < class_code_list.size(); k++) {
                        if (days[i][j].contains(class_code_list.get(k))) {
                            textView_table[i][j].setTextColor(Color.parseColor("#ffffff"));
                            textView_table[i][j].setText(days[i][j]);
                            textView_table[i][j].setBackgroundColor(Color.parseColor(colors.get(k)));
                        }
                    }
                }
            }
        }

    }

    public void initColors() {
        colors.add("#A7EEFF");
        colors.add("#8282EB");
        colors.add("#9BFA73");
        colors.add("#FF5675");
        colors.add("#3DFF92");
        colors.add("#D65BC1");
        colors.add("#CD2E57");
        colors.add("#FFE146");
        colors.add("#FAC87D");
        colors.add("#FF7F50");
        colors.add("#EA9A56");
        colors.add("#FF3232");
        colors.add("#AD6E6E");
        colors.add("#A06641");
        colors.add("#A33CD6");
        colors.add("#8B4513");
        colors.add("#8d508d");
        colors.add("#be32be");
        colors.add("#288C28");
        colors.add("#7AF67A");
        colors.add("#506EA5");
        colors.add("#D2D2FF");
        colors.add("#46B8FF");
        colors.add("#B4FBFF");
        colors.add("#B0A0CD");
        colors.add("#C1FF6B");
        colors.add("#2ABCB4");
        colors.add("#6DD66D");
        colors.add("#FF7E9D");
        colors.add("#FF28A7");
    }
}
