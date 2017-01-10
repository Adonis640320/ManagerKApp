package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STPoint;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.04.
 */
public class PointListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;
    boolean isManager = false;

    ArrayList<STPoint> m_points = null;

    public PointListAdapter(Context context, boolean isManager1) {
        mctx = context;
        isManager = isManager1;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STPoint> points)
    {
        m_points = points;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_points == null)
            return 0;

        return m_points.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        try
        {
            STPoint anItem = m_points.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_point, null);
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblMessage = (TextView) convertView.findViewById(R.id.lblMessage);
                TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
                TextView lblPointValue = (TextView) convertView.findViewById(R.id.lblPointValue);

                lblMessage.setText(anItem.f_title);
                lblDate.setText(Functions.getDateString(anItem.f_datetime));
                lblPointValue.setText(String.format(mctx.getString(R.string.point_value_templ), anItem.f_point));
                if (isManager) {
                    lblPointValue.setTextColor(mctx.getResources().getColor(R.color.clr_blue_light));
                } else {
                    lblPointValue.setTextColor(mctx.getResources().getColor(R.color.clr_red_dark));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }
}
