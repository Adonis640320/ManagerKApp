package com.sincere.kboss.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STNotice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by developer on 2016-10-04.
 */
public class NoticeAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<STNotice> mList;

    public NoticeAdapter(Context context, ArrayList<STNotice> list){
        mContext = context;
        mList = list;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View v, ViewGroup parent) {
        if(v == null) v = inflater.inflate(R.layout.item_notice_group, null);
        TextView titleTv = (TextView)v.findViewById(R.id.title_tv);
//commented by Adonis
//        TextView tvDate = (TextView)v.findViewById(R.id.tvDate);

        Log.e("test", "ElapsedTime:"+Functions.getElapsedHoursFromToday(mList.get(groupPosition).f_date));
        if(Functions.getElapsedHoursFromToday(mList.get(groupPosition).f_date) < 24)
            titleTv.setText(mList.get(groupPosition).f_title+"     N");
        else titleTv.setText(mList.get(groupPosition).f_title);
//commented by Adonis
//        tvDate.setText(Functions.changeDateTimeForm1(mList.get(groupPosition).f_date));

//        if(isExpanded){
//            titleTv.setTextColor(Color.parseColor("#D7242C"));
//        }else{
//            titleTv.setTextColor(Color.parseColor("#000000"));
//        }

        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View v, ViewGroup parent) {
        if(v == null) {
            v = inflater.inflate(R.layout.item_notice_child, null);
        }
            TextView contentTv = (TextView)v.findViewById(R.id.content_tv);
            contentTv.setText(mList.get(groupPosition).f_content);
            if(mList.get(groupPosition).f_content.isEmpty()) {
                contentTv.setVisibility(View.GONE);
            } else contentTv.setVisibility(View.VISIBLE);

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }
}