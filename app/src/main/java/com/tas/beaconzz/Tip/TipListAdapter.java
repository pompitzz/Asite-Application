package com.tas.beaconzz.Tip;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tas.beaconzz.R;
import java.util.List;

//NoticeListAdapter를 그대로 복사후 변경하였음 , 강의목록을 DB에서 뽑아서 나타내기 위한 Adapter
public class TipListAdapter extends BaseAdapter {
    private Context context;
    private List<Tip> tipList;
    private Fragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    public TipListAdapter(Context context, List<Tip> tipList, Fragment parent) { //parent 추가
        this.context = context;
        this.tipList = tipList;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return tipList.size();
    }

    @Override
    public Object getItem(int i) {
        return tipList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_tip, null); //course layout을 참조한다.
        TextView campusMap =(TextView) v.findViewById(R.id.campusMap);
        TextView storeName =(TextView) v.findViewById(R.id.storeName);
        TextView storeContent =(TextView) v.findViewById(R.id.storeContent);
        TextView storeNote =(TextView) v.findViewById(R.id.storeNote);
        campusMap.setText(tipList.get(i).getCampusMap());
        storeName.setText(tipList.get(i).getStoreName());
        storeContent.setText(tipList.get(i).getStoreContent());
        storeNote.setText(tipList.get(i).getStoreNote());
        return v;

    }
}
