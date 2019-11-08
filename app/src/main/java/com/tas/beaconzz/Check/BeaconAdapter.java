package com.tas.beaconzz.Check;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tas.beaconzz.Check.ValidateCheck;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.tas.beaconzz.Floating.FloatingViewService;
import com.tas.beaconzz.Floating.FloatingViewTwoService;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.getActivity;

//현재 시간의 시간표와 동일한 비콘을 스캔하게 해주는 Adapter
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {
    private static final int REQ_CODE_OVERLAY_PERMISSION = 1;
    public static String startTime;
    public static  String minor;
    private List<Beacon> beacons;
    private Fragment parentt;
    public BeaconAdapter(List<Beacon> beacons, final Fragment parentt) {
        this.beacons = beacons;
        this.parentt = parentt;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon_list, parent, false);
        Button addButton = (Button) view.findViewById(R.id.searchButton);
        beacons.get(viewType).getMinor();

        minor = String.valueOf(beacons.get(viewType).getMinor());
        AlertDialog.Builder builder = new AlertDialog.Builder(parentt.getActivity());
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
        String timecourse = "";
        String timeGet = "12:00";
        String timeEnd = "";
        if (MainActivity.userTime.indexOf(korDayOfweek) > -1) {
            int i = MainActivity.userTime.indexOf(korDayOfweek);
            timecourse = MainActivity.userTime.substring(i, MainActivity.userTime.length() - 1);
            timeGet = timecourse.substring(timecourse.indexOf("(") + 1, timecourse.indexOf("(") + 6);
            timeEnd = timecourse.substring(timecourse.indexOf("(") + 7, timecourse.indexOf("(") + 12);
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long min = 0;
        try {
            Date d1 = dateFormat.parse(timeGet);
            Date d2 = dateFormat.parse(timeEnd);
            long duration = d2.getTime() - d1.getTime();
            min = duration / 60000;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long finalMin = min;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (Settings.canDrawOverlays(view.getContext())) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String getTime = sdf.format(date);
                                    startTime = getTime;
                                    if(finalMin >60){
                                        Intent intent = new Intent(view.getContext(), FloatingViewTwoService.class);
                                        view.getContext().startService(intent);
                                    }else{
                                    Intent intent = new Intent(view.getContext(), FloatingViewService.class);
                                    view.getContext().startService(intent);}
                                } else {
                                    Toast.makeText(view.getContext(), "출석체크는 한번만 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat getMonth = new SimpleDateFormat("MM-dd");
                    String getTime = getMonth.format(date);
                    ValidateCheck validateCheck = new ValidateCheck(MainActivity.userID, MainActivity.userTitle + MainActivity.userDivide, getTime, responseListener); // 실질적인 접속 명령어
                    RequestQueue queue = Volley.newRequestQueue(view.getContext()); //Request를 실질적으로 보내는 Queue
                    queue.add(validateCheck);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + view.getContext().getPackageName()));
                    parentt.startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                }
            }
        });
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String courseroomNum = "";
        switch (String.valueOf(beacons.get(position).getMajor()).substring(0,1)){
            case "1":
                courseroomNum = "A";
                break;
            case "2":
                courseroomNum = "B";
                break;
            case "3":
                courseroomNum = "C";
                break;
            case "4":
                courseroomNum = "D";
                break;
            case "5":
                courseroomNum = "E";
                break;
        }
        String roomnum = courseroomNum + "-" + String.valueOf(beacons.get(position).getMajor()).substring(1,3);
        holder.tvMinor.setText("강 의 실 : "+ roomnum+" "+String.valueOf(beacons.get(position).getMinor()));
        holder.room.setText("강의실을 확인하세요!!");

    }
    void addItems(List<Beacon> beacons) {
        if (this.beacons != null) {
            this.beacons.clear();
            this.beacons.addAll(beacons);
            notifyDataSetChanged(); } }
    void reset() {
        if (beacons != null)
            beacons.clear();
        notifyDataSetChanged(); }
    @Override
    public int getItemCount() {
        return beacons.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMinor;
        private TextView room;


        ViewHolder(View itemView) {
            super(itemView);

            tvMinor = itemView.findViewById(R.id.tv_minor);
            room = itemView.findViewById(R.id.room);

        }
    }

}