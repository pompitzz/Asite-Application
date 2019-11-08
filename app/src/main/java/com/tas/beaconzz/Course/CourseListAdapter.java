package com.tas.beaconzz.Course;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;
import com.tas.beaconzz.Schedule.Schedule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
//학과 및 학년 등 조건에 맞는 강의들을 생성하여 강의를 추가할수 있게해주는 Adapter
public class CourseListAdapter extends BaseAdapter {
    private Context context;
    private List<Course> courseList;
    private Fragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    private  String userID = MainActivity.userID;
    private Schedule schedule;
    private  List<Integer> courseIDList; // courseID가 중복 되는지 검사 하기 위해
    public CourseListAdapter(Context context, List<Course> courseList, Fragment parent) { //parent 추가
        this.context = context;
        this.courseList = courseList;
        this.parent = parent;
        schedule = new Schedule();
        courseIDList = new ArrayList<Integer>();
        new BackgroundTask().execute();
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_course, null); //course layout을 참조한다.
        TextView courseGrade =(TextView) v.findViewById(R.id.courseGrade);
        TextView courseTitle =(TextView) v.findViewById(R.id.courseTitle);
        TextView courseCredit =(TextView) v.findViewById(R.id.courseCredit);
        TextView courseDivide =(TextView) v.findViewById(R.id.courseDivide);
        TextView courseRoom =(TextView) v.findViewById(R.id.courseRoom);
        TextView courseProfessor =(TextView) v.findViewById(R.id.courseProfessor);
        TextView courseTime =(TextView) v.findViewById(R.id.courseTime);
        if(courseList.get(i).getCourseGrade().equals("0")|| courseList.get(i).getCourseGrade().equals("")){
            courseGrade.setText("모든 학년");
        }
        else{
            courseGrade.setText(courseList.get(i).getCourseGrade() + "학년");


        }
        courseTitle.setText(courseList.get(i).getCourseTitle());
        courseCredit.setText(courseList.get(i).getCourseCredit() + "학점");
        courseDivide.setText(courseList.get(i).getCourseDivide() + "분반");

        String GetRoom =courseList.get(i).getCourseRoom();
        courseRoom.setText(GetRoom);


        if(courseList.get(i).getCourseProfessor().equals("")){
            courseProfessor.setText("개인 연구");
        }
        else{
        courseProfessor.setText(courseList.get(i).getCourseProfessor() + "교수님");}
        //화:[2]-[3](10:00-11:50)수:[7](15:00-15:50)
        String courseGet = courseList.get(i).getCourseTime();
        if(courseGet.length()>=40){
        String courseGetTime = courseGet.substring(0,courseGet.indexOf(")")+1) +"\n"+ courseGet.substring(courseGet.indexOf(")")+2,courseGet.length()-0);
        courseTime.setText(courseGetTime);}
        else{courseTime.setText(courseGet);}


        v.setTag(courseList.get(i).getCourseID());

        Button addButton = (Button) v.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
                boolean validate = false; // 현재 강의가 추가되는지의 타탕성 검증
                String userID = MainActivity.userID; validate = schedule.validate(courseList.get(i).getCourseTime()); // 강의 시간표
                if(!alreadyIn(courseIDList, courseList.get(i).getCourseID())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("이미 추가한 강의입니다..")
                            .setPositiveButton("다시 시도", null)
                            .create(    ); dialog.show(); }
                else if(validate == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("시간표가 중복됩니다..")
                            .setPositiveButton("다시 시도", null)
                            .create(); dialog.show(); }
                else{ Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("강의가 추가되었습니다.")
                                            .setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();
                                    courseIDList.add(courseList.get(i).getCourseID());
                                    schedule.addSchedule(courseList.get(i).getCourseTime());
                                    new MainActivity.BackgroundTask().execute();
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("강의 추가에 실패하였습니다.")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddRequest addRequest = new AddRequest(userID, courseList.get(i).getCourseID() + "",courseList.get(i).getCourseTitle() + "",courseList.get(i).getCourseProfessor()+"",courseList.get(i).getCourseTime()+"",courseList.get(i).getCourseDivide()+"",courseList.get(i).getCourseRoom(),responseListener);
                    RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                    queue.add(addRequest);
                }
            }

        });

        return v;

    }
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;
        @Override
        protected  void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/ScheduleList.php?userID=" + URLEncoder.encode(userID, "UTF-8");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return  stringBuilder.toString().trim();

            }catch (Exception e){
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        public  void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

        @Override
        public  void onPostExecute(String result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                   String courseTime;
                   int courseID;
                while (count < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseID = object.getInt("courseID");
                    courseTime = object.getString("courseTime");
                    courseIDList.add(courseID);
                    schedule.addSchedule(courseTime);
                    count++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean alreadyIn(List<Integer> courseIDList, int item) //모든 리스트의 id를 돌면서 courseID가 중복되는지 확인
    {
        for(int i = 0; i< courseIDList.size(); i++)
        {
            if(courseIDList.get(i) ==item)
            {
                return false;
            }
        }
        return true;
    }
}
