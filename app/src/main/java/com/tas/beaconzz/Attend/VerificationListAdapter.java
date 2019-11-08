package com.tas.beaconzz.Attend;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tas.beaconzz.Schedule.GetSchedule;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;
import com.tas.beaconzz.Schedule.Schedule;

import java.util.List;

//출석 조회가능한 강의들을 각각 표시해주는 Adapter
public class VerificationListAdapter extends BaseAdapter {
    private Context context;
    private List<GetSchedule> scheduleList;
    private VerificationFragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    private Schedule schedule;
    public VerificationListAdapter(Context context, List<GetSchedule> scheduleList, VerificationFragment parent) { //parent 추가
        this.context = context;
        this.scheduleList = scheduleList;
        this.parent = parent;
        schedule = new Schedule();
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return scheduleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_verification, null); //course layout을 참조한다.
        TextView courseTitle =(TextView) v.findViewById(R.id.courseTitle);
        TextView courseDivide =(TextView) v.findViewById(R.id.courseDivide);
        TextView courseProfessor =(TextView) v.findViewById(R.id.courseProfessor);
        TextView courseTime =(TextView) v.findViewById(R.id.courseTime);
        courseTitle.setText(scheduleList.get(i).getCourseTitle());
        courseDivide.setText(scheduleList.get(i).getCourseDivide() + "분반");
        courseProfessor.setText(scheduleList.get(i).getCourseProfessor() + "교수님");

        String courseGet = scheduleList.get(i).getCourseTime();
        if(courseGet.length()>=40){
        String courseGetTime = courseGet.substring(0,courseGet.indexOf(")")+1) +"\n"+ courseGet.substring(courseGet.indexOf(")")+2,courseGet.length()-0);
        courseTime.setText(courseGetTime);}
        else{courseTime.setText(courseGet);}
        final String checkTitle = scheduleList.get(i).getCourseTitle();
        final String checkDivide = scheduleList.get(i).getCourseDivide();
        final String checkTime = scheduleList.get(i).getCourseTime();



        Button deleteBoard = (Button) v.findViewById(R.id.deleteButton);
        deleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext() , AttendActivity.class);
                intent.putExtra("checkTitle",checkTitle);
                intent.putExtra("checkDivide",checkDivide);
                intent.putExtra("checkTime",checkTime);
                view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return v;

    }
}
