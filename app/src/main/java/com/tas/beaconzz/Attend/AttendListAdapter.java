package com.tas.beaconzz.Attend;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tas.beaconzz.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//출석 조회시 각각 학생들의 값들을 표시
public class AttendListAdapter extends BaseAdapter {
    private Context context;
    private List<Attend> attendList;
    public AttendListAdapter(Context context, List<Attend> attendList) { //parent 추가
        this.context = context;
        this.attendList = attendList;
    }

    @Override
    public int getCount() {
        return attendList.size();
    }

    @Override
    public Object getItem(int i) {
        return attendList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_attend, null);
        TextView attendDate = (TextView) v.findViewById(R.id.attendDate);
        TextView attend = (TextView) v.findViewById(R.id.attend);
        TextView state = (TextView) v.findViewById(R.id.state);
        TextView attendStart = (TextView) v.findViewById(R.id.attendStart);
        TextView attendEnd = (TextView) v.findViewById(R.id.attendEnd);

        attendStart.setText(attendList.get(i).getAttendStart());
        attendEnd.setText(attendList.get(i).getAttendEnd());

        String attendDance = attendList.get(i).getAttendDance();
        String attendState = attendList.get(i).getAttendState();
        try {
            String getDate = getDateDay("2019-"+attendList.get(i).getAttendDate(),"yyyy-MM-dd");
            attendDate.setText(attendList.get(i).getAttendDate() + getDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(attendDance.equals("출석")){
            attend.setText("출석 처리");
        }else if(attendDance.equals("지각")){
            attend.setText("지각 처리");
            attend.setTextColor(Color.RED);
        }else{
            attend.setText("결석 처리");
            attend.setTextColor(Color.RED);
        }

        if(attendState.equals("빠른 종료")){
            state.setText("빠른 종료");
            state.setTextColor(Color.RED);
        }else if(attendState.equals("정상 종료")){
            state.setText("정상 종료");
        }else{
            state.setText("결석 처리");
            state.setTextColor(Color.RED);
        }

        return v;

    }
    public static String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "(일)";
                break;
            case 2:
                day = "(월)";
                break;
            case 3:
                day = "(화)";
                break;
            case 4:
                day = "(수)";
                break;
            case 5:
                day = "(목)";
                break;
            case 6:
                day = "(금)";
                break;
            case 7:
                day = "(토)";
                break;

        }
        return day;
    }
}
