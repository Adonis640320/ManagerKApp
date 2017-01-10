package com.sincere.kboss.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STWeekday;
import com.sincere.kboss.stdata.STWorkingArea;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.09.08.
 */
public class WeekdayListAdapter extends BaseAdapter {
    LayoutInflater mInflater;

    Context mContext = null;
    Activity mActivity = null;

    ArrayList<STWeekday> m_weekdays = null;
    ArrayList<Boolean> m_chkStats = new ArrayList<>();

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int pos = (Integer) buttonView.getTag();

            m_chkStats.set(pos, isChecked);

            recalcAllChecked();

            notifyDataSetChanged();
        }
    };

    CompoundButton.OnCheckedChangeListener checkedAllChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (int i=0; i<m_chkStats.size(); i++) {
                m_chkStats.set(i, isChecked);
            }

            notifyDataSetChanged();
        }
    };

    public WeekdayListAdapter(Activity activity, Context context) {
        mContext = context;
        mActivity = activity;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STWeekday> weekdays)
    {
        boolean allfalse = false;
        m_weekdays = weekdays;

        String prefer_time = KbossApplication.g_userinfo.f_prefer_time;
        if (prefer_time.equals("0")) {
            allfalse = true;
        }

        if (m_weekdays != null ) {
            for (int i=0; i<m_weekdays.size()+1; i++) {
                m_chkStats.add(allfalse);
            }
        }

        if (allfalse == false) {
            String[] weekdayids = prefer_time.split(",");
            for (int i = 0; i < weekdayids.length; i++) {
                int weekdayid = Integer.parseInt(weekdayids[i]);
                m_chkStats.set(weekdayid, true);
            }
        }
    }

    public void recalcAllChecked() {
        boolean allCheck = true;

        for (int i=1; i<m_chkStats.size(); i++) {
            allCheck = allCheck && m_chkStats.get(i);
        }

        m_chkStats.set(0, allCheck);
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_weekdays == null)
            return 1;

        return m_weekdays.size()+1;
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
            STWeekday  anItem = null;
            if (position > 0)
            {
                anItem = m_weekdays.get(position-1);
            }

            //if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_text_n_check, null);
                convertView.setTag(position);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = (Integer) view.getTag();
                        if(pos == 0) {
                            boolean isChecked = m_chkStats.get(0);
                            for (int i = 0; i < m_chkStats.size(); i++) {
                                m_chkStats.set(i, !isChecked);
                            }
                        } else {
                            m_chkStats.set(pos, !m_chkStats.get(pos));
                            recalcAllChecked();
                        }
                        notifyDataSetChanged();
                    }
                });
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblAreaName = (TextView) convertView.findViewById(R.id.lblAreaName);
                CheckBox chkArea = (CheckBox) convertView.findViewById(R.id.chkArea);
                TextView lblSep = (TextView) convertView.findViewById(R.id.lblSep);

                chkArea.setTag(position);
                chkArea.setChecked(m_chkStats.get(position));

                if (position == 0) {
                    lblAreaName.setText(mActivity.getString(R.string.personalinfosetting_everyday));
                    chkArea.setOnCheckedChangeListener(checkedAllChangeListener);

                } else {
                    lblAreaName.setText(anItem.f_weekday);
                    chkArea.setOnCheckedChangeListener(checkedChangeListener);

                    if (position == m_weekdays.size()) {
                        lblSep.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
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

    public String getWeekdayString() {
        if (m_chkStats.size() == 0) {
            return "";
        }

        if (m_chkStats.get(0) == true) {
            return "0";
        } else {
            String ret = "";
            for (int i=1; i<m_chkStats.size(); i++) {
                boolean b = m_chkStats.get(i);
                if (b) {
                    ret += String.valueOf(i) + ",";
                }
            }

            if (!ret.isEmpty() && ret.charAt(ret.length()-1)==',') {
                ret = ret.substring(0, ret.length()-1);
            }

            return ret;
        }
    }
}
