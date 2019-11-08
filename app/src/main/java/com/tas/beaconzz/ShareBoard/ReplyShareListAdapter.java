package com.tas.beaconzz.ShareBoard;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tas.beaconzz.Board.BoardContent;
import com.tas.beaconzz.Board.Reply;
import com.tas.beaconzz.Board.ReplyDeleteRequest;
import com.tas.beaconzz.NoticeAndMain.MainActivity;
import com.tas.beaconzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//NoticeListAdapter를 그대로 복사후 변경하였음 , 강의목록을 DB에서 뽑아서 나타내기 위한 Adapter
public class ReplyShareListAdapter extends BaseAdapter {
    private Context context;
    private List<ReplyShare> replyShareList;
    private Activity parent;
    public ReplyShareListAdapter(Context context, List<ReplyShare> replyShareList, Activity parent) { //parent 추가
        this.context = context;
        this.replyShareList = replyShareList;
        this.parent = parent;

    }

    @Override
    public int getCount() {
        return replyShareList.size();
    }

    @Override
    public Object getItem(int i) {
        return replyShareList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v= View.inflate(context, R.layout.row_reply, null); //course layout을 참조한다.
        TextView replyContent =(TextView) v.findViewById(R.id.replyContent);
        TextView replyName =(TextView) v.findViewById(R.id.replyName);
        TextView replyDate =(TextView) v.findViewById(R.id.replyDate);
        TextView replyDeleteButton = (TextView) v.findViewById(R.id.replyDeleteButton);

        replyName.setText(replyShareList.get(i).getReplyName());
        replyContent.setText(replyShareList.get(i).getReplyContent());
        replyDate.setText(replyShareList.get(i).getReplyDate());

        replyDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.userIDName.equals(replyShareList.get(i).getReplyName())){
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try
                            {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                                    AlertDialog dialog = builder.setMessage("삭제성공")
                                            .setPositiveButton("확인",null)
                                            .create();
                                    dialog.show();
                                    ((ShareContent)context).resetGraph(context);
                                } }
                            catch (JSONException e){
                                e.printStackTrace(); } }};
                    ReplyShareDeleteRequest replyShareDeleteRequest= new ReplyShareDeleteRequest(replyShareList.get(i).getReplyNumber(), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(v.getContext());
                    queue.add(replyShareDeleteRequest);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("게시자만 삭제가 가능합니다.")
                            .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which){
                                }
                            }).create()
                            .show();
                }
            }
        });
        return v;

    }

}
