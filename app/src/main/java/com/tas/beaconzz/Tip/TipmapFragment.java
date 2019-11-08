package com.tas.beaconzz.Tip;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.tas.beaconzz.Course.Course;
import com.tas.beaconzz.Course.CourseFragment;
import com.tas.beaconzz.Course.CourseListAdapter;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

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

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TipmapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TipmapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TipmapFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String getCategory;
    private OnFragmentInteractionListener mListener;


    public TipmapFragment() {
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
    public static TipmapFragment newInstance(String param1, String param2) {
        TipmapFragment fragment = new TipmapFragment();
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
    private ImageView categoryImg;
    private PhotoViewAttacher photoViewAttacher;
    private ListView tipListView; //강의 내역을 파싱하기 위한 것들
    private TipListAdapter adapter;
    private List<Tip> tipList;
    //Activity가 만들어 졌을때 처리 하는 부분
    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);
        categoryImg = (ImageView)  getView().findViewById(R.id.categoryImg);
        getCategory = "";
        Bundle args = getArguments();
        if(args!=null){ getCategory = args.getString("category");}
        tipListView = (ListView)  getView().findViewById(R.id.tipListView);
        tipList = new ArrayList<Tip>();
        adapter = new TipListAdapter(getContext().getApplicationContext(),tipList, this);
        new BackgroundTask().execute();
        tipListView.setAdapter(adapter);
        if(getCategory.equals("ATM")) { categoryImg.setImageResource(R.drawable.atm_map);
        }else if(getCategory.equals("도서관")) { categoryImg.setImageResource(R.drawable.library_map);
        }else if(getCategory.equals("음악감상실")) {categoryImg.setImageResource(R.drawable.music_hall_map);
        }else if(getCategory.equals("보건소")) {categoryImg.setImageResource(R.drawable.helath_office_map);
        }else if(getCategory.equals("복사실")) {categoryImg.setImageResource(R.drawable.copy_room_map);
        }else if(getCategory.equals("카페")) {categoryImg.setImageResource(R.drawable.cafe_map);
        }else if(getCategory.equals("편의점")) {categoryImg.setImageResource(R.drawable.convenience_map);
        }else if(getCategory.equals("편의시설")) {categoryImg.setImageResource(R.drawable.accommodation_map);
        }else if(getCategory.equals("학생회실")) {categoryImg.setImageResource(R.drawable.student_council_map);
        }else if(getCategory.equals("학식")) {categoryImg.setImageResource(R.drawable.school_cafeteria_map);
        }
        photoViewAttacher = new PhotoViewAttacher(categoryImg);
       // photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tipmap, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;
        //URL에 Spinner값을 보내는 것
        @Override
        protected void onPreExecute() {
            try {
                target = "http://dm951125.dothome.co.kr/TipList.php?storeCategory="
                        + URLEncoder.encode(getCategory, "UTF-8");

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
                tipList.clear(); // 해당 강의 목록 초기화
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String campusMap, storeName, storeNote, storeContent;
                //현재 배열 원소값을 가져오게 한다.
                while (count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);

                    campusMap = object.getString("campusMap");
                    storeName = object.getString("storeName");
                    storeNote = object.getString("storeNote");
                    storeContent = object.getString("storeContent");

                    Tip tip = new Tip(campusMap,storeName,storeNote,storeContent);
                    tipList.add(tip);
                    count++;
                }
                adapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}


