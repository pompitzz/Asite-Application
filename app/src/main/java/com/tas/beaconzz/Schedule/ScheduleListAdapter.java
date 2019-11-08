package com.tas.beaconzz.Schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Course.CourseFragment;
import com.tas.beaconzz.LoginAndResister.LoginActivity;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONObject;

import java.util.List;

//NoticeListAdapter를 그대로 복사후 변경하였음 , 강의목록을 DB에서 뽑아서 나타내기 위한 Adapter
public class ScheduleListAdapter extends BaseAdapter {
    private Context context;
    private List<GetSchedule> scheduleList;
    private CourseFragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    private  String userID = MainActivity.userID;
    private Schedule schedule;
    public ScheduleListAdapter(Context context, List<GetSchedule> scheduleList, CourseFragment parent) { //parent 추가
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
        View v= View.inflate(context, R.layout.row_schedule, null); //course layout을 참조한다.
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



        Button deleteBoard = (Button) v.findViewById(R.id.deleteButton);
        deleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = MainActivity.userID;
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                builder.setMessage("시간표 삭제시 출석 내용이 제거 됩니다.\n정말로 삭제 하시겠습니까?")
                        .setPositiveButton("예",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int which){
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                                AlertDialog dialog = builder.setMessage("제거완료")
                                                        .setPositiveButton("확인", null)
                                                        .create();
                                                dialog.show();
                                                parent.resetGraph(context);
                                                new MainActivity.BackgroundTask().execute();
                                            }
                                            else {
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                ScheduleDeleteRequest scheduleDeleteRequest = new ScheduleDeleteRequest(userID, scheduleList.get(i).getCourseTitle() + "",scheduleList.get(i).getCourseDivide()+"",responseListener);
                                RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                                queue.add(scheduleDeleteRequest);
                            }
                        }).setNegativeButton("아니오",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int which){
                            }
                 }).setCancelable(false)
                        .create()
                        .show();

            }
        });

        return v;

    }
}
