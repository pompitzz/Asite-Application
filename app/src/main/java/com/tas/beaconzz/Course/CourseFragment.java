package com.tas.beaconzz.Course;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.tas.beaconzz.MyServer;
import com.tas.beaconzz.Schedule.GetSchedule;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;
import com.tas.beaconzz.Schedule.ScheduleListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//강의 목록등을 나타내기 위한 Fragment
public class CourseFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public CourseFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }
    //Spinner 변수

    private ArrayAdapter gradeAdapter;
    private Spinner gradeSpinner;
    private ArrayAdapter majorAdapter;
    private Spinner majorSpinner;
    //학부 대학원
    private String courseUniversity = "";
    private String Grade;
    private int Year, Semester;
    private ListView courseListView; //강의 내역을 파싱하기 위한 것들
    private CourseListAdapter adapter;
    private List<Course> courseList;
    private ScheduleListAdapter scheduleAdapter;
    private List<GetSchedule> scheduleList;
    //Activity가 만들어 졌을때 처리 하는 부분
    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);

        final RadioGroup courseUniversityGroup = (RadioGroup) getView().findViewById(R.id.courseUniversityGroup); //선택한게 학부인지 대학원 인지
        //Spinner 값 매칭
        majorSpinner = (Spinner) getView().findViewById(R.id.majorSpinner);
        gradeSpinner = (Spinner) getView().findViewById(R.id.gradeSpinner);
        majorAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Empty,android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);
        //RadioGroup 내용에 따라 실행하는 부분
        courseUniversityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton courseButton = (RadioButton) getView().findViewById(i); // 현재 선택한 RadioButton 값
                courseUniversity = courseButton.getText().toString();
                if(courseUniversity.equals("시간표 등록")){
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.universityMajor,android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                    gradeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Grade,android.R.layout.simple_spinner_dropdown_item);
                    gradeSpinner.setAdapter(gradeAdapter );

                }
                if(courseUniversity.equals("시간표 삭제")){
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.deleteCourse,android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                    gradeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Empty,android.R.layout.simple_spinner_dropdown_item);
                    gradeSpinner.setAdapter(gradeAdapter );
                }


            }
        });



        scheduleList = new ArrayList<GetSchedule>();
        scheduleAdapter = new ScheduleListAdapter(getContext().getApplicationContext(),scheduleList, this);

        Grade = "0";
        LocalDate localDate = LocalDate.now();
        Year = localDate.getYear();
        Semester = localDate.getMonthValue() < 8 ? 1 : 2;
        Button searchButton = (Button) getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CourseListAdapter 와 일치 시키는 부분
                courseListView = (ListView)  getView().findViewById(R.id.courseListView);
                courseList = new ArrayList<Course>();
                adapter = new CourseListAdapter(getContext().getApplicationContext(),courseList, CourseFragment.this);
                if(gradeSpinner.getSelectedItemPosition()>0){
                    Grade = gradeSpinner.getSelectedItem().toString().substring(0,1);
                }else{
                    Grade = "0";
                }
                if(majorSpinner.getSelectedItem().equals("시간표 삭제")) {
                    courseListView.setAdapter(scheduleAdapter);
                    new BackgroundTask2().execute();
                }else if (majorSpinner.getSelectedItem().equals("")) {
                    Toast.makeText(v.getContext(),"등록 혹은 삭제를 선택해주세요",Toast.LENGTH_SHORT).show();
                }else{
                        courseListView.setAdapter(adapter);
                        new BackgroundTask().execute(); }

                }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void resetGraph(Context context) {
        new CourseFragment.BackgroundTask2().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;
        //URL에 Spinner값을 보내는 것
        @Override
        protected void onPreExecute() {
            try {
                target = MyServer.url + "?year="
                        + URLEncoder.encode(String.valueOf(Year), "UTF-8")
                        + "&semester=" + URLEncoder.encode(String.valueOf(Semester), "UTF-8")
                        + "&grade=" + URLEncoder.encode(Grade, "UTF-8")
                        + "&major=" + URLEncoder.encode(majorSpinner.getSelectedItem().toString(), "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        //특정 강의 학과를 넣었을때 모든 강의 리스트를 불러오는 부분
        @Override
        public void onPostExecute(String result) {
            try {
                courseList.clear(); // 해당 강의 목록 초기화
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                int courseID; int courseCredit; int courseDivide;
                String courseTerm;  String courseGrade; String courseTitle;
                String courseRoom; String courseProfessor; String courseTime;
                //현재 배열 원소값을 가져오게 한다.
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseID = object.getInt("id");
                    courseTerm = object.getString("semester");
                    courseGrade = object.getString("grade");
                    courseTitle = object.getString("title");
                    courseCredit = object.getInt("credit");
                    courseDivide = object.getInt("number");
                    courseRoom = object.getString("location");
                    courseProfessor = object.getString("professor");
                    courseTime = object.getString("time");

                    Course course = new Course(courseID, courseTerm, courseGrade, courseTitle, courseCredit, courseDivide, courseRoom, courseProfessor,courseTime);
                    courseList.add(course);
                    count++;
                }
                if(count==0)
                {
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CourseFragment.this.getActivity());
                    dialog = builder.setMessage("조회된 강의가 없습니다.\n 날짜를 확인하세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                }
                adapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class BackgroundTask2 extends AsyncTask<Void, Void, String> {
        String target;
        //URL에 Spinner값을 보내는 것
        @Override
        protected void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/MySchduleList.php?userID="
                        + URLEncoder.encode(MainActivity.userID, "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        //특정 강의 학과를 넣었을때 모든 강의 리스트를 불러오는 부분
        @Override
        public void onPostExecute(String result) {
            try {
                scheduleList.clear(); // 해당 강의 목록 초기화
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;

                String courseDivide;
                String courseTitle;
                String courseProfessor;
                String courseTime;
                //현재 배열 원소값을 가져오게 한다.
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);

                    courseTitle = object.getString("courseTitle");
                    courseDivide = object.getString("courseDivide");
                    courseProfessor = object.getString("courseProfessor");
                    courseTime = object.getString("courseTime");

                    GetSchedule getSchedule = new GetSchedule(courseDivide, courseTitle, courseProfessor, courseTime);
                    scheduleList.add(getSchedule);
                    count++;
                }
                scheduleAdapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}


