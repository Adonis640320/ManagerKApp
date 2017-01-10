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
import com.sincere.kboss.stdata.STWorkShopHstory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SunMS on 12/16/2016.
 */

public class WorkShopHistoryAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STWorkShopHstory> m_workshophistory = null;

    public WorkShopHistoryAdapter(Context context) {
        mctx = context;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STWorkShopHstory> workshophistory)
    {
        m_workshophistory = workshophistory;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_workshophistory == null)
            return 0;

        return m_workshophistory.size();
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
     * @see android.widget.ListAdapter#getView(int, View,
     *      ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        try
        {
            STWorkShopHstory anItem = m_workshophistory.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_log, null);
            //}
            convertView.setTag(position);

            if (KbossApplication.UI_TEST == false) {
                TextView lblName = (TextView) convertView.findViewById(R.id.lblMessage);
                TextView lblDate = (TextView) convertView.findViewById(R.id.lblDateTime);

                if ( anItem.content.length()>0 )
                {
                    lblName.setText(anItem.content);
                    lblDate.setText(anItem.date);
                }
                else {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strNow = formatter.format(new Date());
                    int diffday=(int) Functions.getElapsedDate(anItem.date,strNow);


                    String[] datefrag = anItem.content.split("-");
                    String[] dayfrag = datefrag[2].split(" ");
                    if (diffday == 1) {
                        lblName.setText("내가 어제 일했던 현장입니다.총(" + String.valueOf(anItem.num_id) + ")일");
                    } else if (diffday > 1 && diffday <= 10) {
                        lblName.setText(String.format("내가 %d일전 일했던 현장입니다.총(%d)일", diffday, anItem.num_id));
                    } else if (diffday > 10) {
                        lblName.setText(String.format("내가 최근 일했던 현장입니다.총(%d)일", diffday, anItem.num_id));
                    }
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
