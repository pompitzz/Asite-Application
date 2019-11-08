package com.tas.beaconzz.Check;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;
import com.tas.beaconzz.ShareBoard.ShareContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.estimote.coresdk.common.config.EstimoteSDK.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//비콘을 스캔하기 위해 필요한 Fragment
public class CheckFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ScanActivity";
    private Button btnScan;
    private Button btnReset;
    private TextView courseTitle, courseTime;
    private RecyclerView rvBeaconList;
    private BeaconManager beaconManager;
    private BeaconRegion beaconRegion;
    private BeaconAdapter beaconAdapter;
    private ProgressBar progressBar;
    private String getMainRoom;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CheckFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckFragment newInstance(String param1, String param2) {
        CheckFragment fragment = new CheckFragment();
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
    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);
        btnScan = (Button) getView().findViewById(R.id.btnScan);
        btnReset = (Button) getView().findViewById(R.id.btnReset);
        rvBeaconList = (RecyclerView) getView().findViewById(R.id.rvBeaconList);
        courseTitle = (TextView) getView().findViewById(R.id.courseTitle);
        courseTime = (TextView) getView().findViewById(R.id.courseTime);
        progressBar = (ProgressBar) getView().findViewById(R.id.pb);
        listeners();
        beaconManager = ((MyApp) getApplicationContext()).beaconManager;
        int getMajor = 0; int getMinor = 0;
        new MainActivity.BackgroundTask().execute();
        String buildingNumber = "";
        getMainRoom = MainActivity.userRoom;
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String korDayOfweek = "";
        switch (dayOfWeek) {
            case 1:
                korDayOfweek = "일";
                break;
            case 2:
                korDayOfweek = "월";
                break;
            case 3:
                korDayOfweek = "화";
                break;
            case 4:
                korDayOfweek = "수";
                break;
            case 5:
                korDayOfweek = "목";
                break;
            case 6:
                korDayOfweek = "금";
                break;
            case 7:
                korDayOfweek = "토";
                break;
        }
        if(getMainRoom!=null) {
            if (getMainRoom.length() > 13) {
                if (MainActivity.userTime.indexOf(korDayOfweek) > 5) {
                    getMainRoom = getMainRoom.substring(getMainRoom.indexOf(')') + 1, getMainRoom.length() - 0);
                } else {
                    getMainRoom = getMainRoom.substring(0, getMainRoom.indexOf(')') + 1);
                }
            }
        }

        if(getMainRoom!=null){
            switch (getMainRoom.substring(1,2)){
            case "A":
                buildingNumber ="1" + getMainRoom.substring(2,4);
                break;
            case "B":
                buildingNumber ="2" + getMainRoom.substring(2,4);
                break;
            case "C":
                buildingNumber ="3" + getMainRoom.substring(2,4);
                break;
            case "D":
                buildingNumber ="4" + getMainRoom.substring(2,4);
                break;
            case "E":
                buildingNumber ="5" + getMainRoom.substring(2,4);
                break;}
                getMajor = Integer.parseInt(buildingNumber); getMinor = Integer.parseInt(getMainRoom.substring(5,8));
                courseTitle.setText(MainActivity.userTitle); courseTime.setText(MainActivity.userTime);
        }else{
            courseTitle.setText(""); courseTime.setText("");

        }
        beaconRegion = new BeaconRegion("monitored region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                getMajor, getMinor);
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {

                beaconAdapter.addItems(beacons);
                progressBar.setVisibility(View.GONE);
                btnReset.setEnabled(true);
                Log.d(TAG, "onEnteredRegion: ");
            }
            @Override
            public void onExitedRegion(BeaconRegion region) {
                Toast.makeText(CheckFragment.this.getActivity(), "onExitedRegion", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onExitedRegion: " + region);
            }
        });
        beaconAdapter = new BeaconAdapter(new ArrayList<Beacon>(), this);
        rvBeaconList.setLayoutManager(new LinearLayoutManager(CheckFragment.this.getActivity()));
        rvBeaconList.setAdapter(beaconAdapter);

    }
    private void listeners() {
        btnScan.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }




    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this.getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false);
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                if (btnScan.getText().toString().equals(getString(R.string.scan_beacon))) {
                    if(getMainRoom!=null){
                        beaconAdapter.reset();
                        beaconManager.stopMonitoring(beaconRegion.getIdentifier());
                        SystemRequirementsChecker.checkWithDefaultDialogs(this.getActivity());
                        progressBar.setVisibility(View.VISIBLE);
                        beaconManager.startMonitoring(beaconRegion);
                        btnScan.setText("불러오기 중단");
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        AlertDialog dialog = builder.setMessage("시간표를 확인해 주세요.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();}
                } else if (btnScan.getText().toString().equals(getString(R.string.stop_beacon))) {
                    progressBar.setVisibility(View.GONE);
                    beaconManager.stopMonitoring(beaconRegion.getIdentifier());
                    btnScan.setText(getString(R.string.scan_beacon));
                }
                break;
            case R.id.btnReset:
                beaconAdapter.reset();
                btnScan.setText(getString(R.string.scan_beacon));
                btnReset.setEnabled(false);
                break;
        }
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
}
