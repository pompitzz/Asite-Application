package com.tas.beaconzz.ShareBoard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tas.beaconzz.Board.Board;
import com.tas.beaconzz.Board.BoardContent;
import com.tas.beaconzz.R;

import java.util.List;

//NoticeListAdapter를 그대로 복사후 변경하였음 , 강의목록을 DB에서 뽑아서 나타내기 위한 Adapter
public class ShareListAdapter extends BaseAdapter {
    private Context context;
    private List<Share> shareList;
    private Fragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    public ShareListAdapter(Context context, List<Share> courseList, Fragment parent) { //parent 추가
        this.context = context;
        this.shareList = courseList;
        this.parent = parent;
        Fresco.initialize(context);

    }

    @Override
    public int getCount() {
        return shareList.size();
    }

    @Override
    public Object getItem(int i) {
        return shareList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_share, null); //course layout을 참조한다.
        TextView boardName =(TextView) v.findViewById(R.id.boardName);
        TextView boardTitle =(TextView) v.findViewById(R.id.boardTitle);
        TextView boardDate =(TextView) v.findViewById(R.id.boardDate);
        TextView boardContent =(TextView) v.findViewById(R.id.boardContent);

        LinearLayout board =(LinearLayout) v.findViewById(R.id.board);
        boardContent.setText(shareList.get(i).getBoardContent());
        boardName.setText(shareList.get(i).getBoardName());
        boardTitle.setText(shareList.get(i).getBoardTitle());
        boardDate.setText(shareList.get(i).getBoardDate().substring(0,10));
        SimpleDraweeView draweeView = (SimpleDraweeView) v.findViewById(R.id.boardImage);
        if(!shareList.get(i).getBoardImageUrl().equals("empty")){
            Uri uri = Uri.parse("http://dm951125.dothome.co.kr/"+shareList.get(i).getBoardImageUrl());
            draweeView.setImageURI(uri);
        }else{
            draweeView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(parent.getContext() , ShareContent.class);
                intent.putExtra("boardTitle",shareList.get(i).getBoardTitle());
                intent.putExtra("boardContent",shareList.get(i).getBoardContent());
                intent.putExtra("userName",shareList.get(i).getBoardName());
                intent.putExtra("boardDate",shareList.get(i).getBoardDate());
                intent.putExtra("boardNumber",shareList.get(i).getBoardNumber());
                intent.putExtra("boardImageUrl",shareList.get(i).getBoardImageUrl());

                parent.startActivity(intent);
            } });
        return v;

    }

}
