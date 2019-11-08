package com.tas.beaconzz.NoticeAndMain;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tas.beaconzz.R;
import com.tas.beaconzz.databinding.ItemNewsBinding;

import java.util.List;

public class NoticeAdapter extends BaseAdapter {
    private List<MainActivity.News> mData;
    private ItemNewsBinding mmBinding;

    public NoticeAdapter(List<MainActivity.News> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainActivity.ViewHolder holder;
        if (convertView == null) {
            mmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_news, parent, false);

            convertView = mmBinding.getRoot();

            holder = new MainActivity.ViewHolder();

            holder.titleTextView = mmBinding.titleText;
            holder.dateTextView = mmBinding.dateText;
            holder.descriptionView = mmBinding.descriptionText;
            holder.nameTextView = mmBinding.nameText;

            convertView.setTag(holder);
        } else {
            holder = (MainActivity.ViewHolder) convertView.getTag();
        }
        final MainActivity.News news = (MainActivity.News) getItem(position);
        holder.titleTextView.setText(news.title);
        holder.dateTextView.setText(news.pubDate);
        holder.nameTextView.setText(news.name);
        holder.descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(news.link));
                v.getContext().startActivity(intent);
            }
        });
        news.description = news.description.replace("&nbsp;;", "");
        news.description = news.description.replace("&middot;", "");
        news.description = news.description.replace("&lsquo;", "");
        news.description = news.description.replace("&lt;", "");
        news.description = news.description.replace("&gt;", "");
        news.description = news.description.replace("&nbsp", "");
        news.description = news.description.replace("&rsquo;", "");
        news.description = news.description.replaceAll("[*]", "");
        news.description = news.description.replaceAll("[;]", "");
        news.description = news.description.replace("&ldquo;", "");
        news.description = news.description.replace("&quot;", "");
        news.description = news.description.replace("&quot", "");
        news.description = news.description.replace("&rdquo;", "");
        news.description = news.description.replaceAll("(\r\n|\r|\n|\n\r)", " ");
        news.title = news.title.replace("...", "");

        holder.descriptionView.setText(news.description);
        return convertView;
    }
}