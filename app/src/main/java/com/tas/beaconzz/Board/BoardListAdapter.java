package com.tas.beaconzz.Board;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tas.beaconzz.R;

import java.util.List;

//게시판들의 각각 목록들이 생성되는 Adapter
public class BoardListAdapter extends BaseAdapter {
    private Context context;
    private List<Board> boardList;
    private Fragment parent; // 자신을 불어낸 부모 fragment 를 담기 위해
    public BoardListAdapter(Context context, List<Board> courseList, Fragment parent) { //parent 추가
        this.context = context;
        this.boardList = courseList;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return boardList.size();
    }

    @Override
    public Object getItem(int i) {
        return boardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_board, null); //course layout을 참조한다.
        TextView boardName =(TextView) v.findViewById(R.id.boardName);
        TextView boardContent =(TextView) v.findViewById(R.id.boardContent);

        TextView boardTitle =(TextView) v.findViewById(R.id.boardTitle);
        TextView boardDate =(TextView) v.findViewById(R.id.boardDate);
        LinearLayout board =(LinearLayout) v.findViewById(R.id.board);

        boardContent.setText(boardList.get(i).getBoardContent());
        boardName.setText(boardList.get(i).getBoardName());
        boardTitle.setText(boardList.get(i).getBoardTitle());
        boardDate.setText(boardList.get(i).getBoardDate().substring(0,10));
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(parent.getContext() , BoardContent.class);
                intent.putExtra("boardTitle",boardList.get(i).getBoardTitle());
                intent.putExtra("boardContent",boardList.get(i).getBoardContent());
                intent.putExtra("userName",boardList.get(i).getBoardName());
                intent.putExtra("boardDate",boardList.get(i).getBoardDate());
                intent.putExtra("boardNumber",boardList.get(i).getBoardNumber());
                parent.startActivity(intent);
            } });
        return v;

    }

}
