package com.sincere.kboss.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STNotice;

import java.util.ArrayList;

/**
 * Created by developer on 2016-10-04.
 */
public class NoticeListAdapter extends BaseAdapter {


    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<STNotice> mList;

    public NoticeListAdapter(Context context, ArrayList<STNotice> list) {
        mContext = context;
        mList = list;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(mList == null) return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) view = inflater.inflate(R.layout.item_notice_group, null);
        TextView titleTv = (TextView)view.findViewById(R.id.title_tv);
        //commented by Adonis
//        TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
        ImageView imgBadge = (ImageView)view.findViewById(R.id.imgBadge);
        Log.e("test", "ElapsedTime:"+Functions.getElapsedHoursFromToday(mList.get(i).f_date));
        if(Functions.getElapsedHoursFromToday(mList.get(i).f_date) < 24) {
            titleTv.setText(mList.get(i).f_title);
            imgBadge.setVisibility(View.VISIBLE);
        } else {
            titleTv.setText(mList.get(i).f_title);
            imgBadge.setVisibility(View.GONE);
        }
        // commented by Adonis
//        tvDate.setText(Functions.changeDateTimeForm1(mList.get(i).f_date));


        return view;
    }
}