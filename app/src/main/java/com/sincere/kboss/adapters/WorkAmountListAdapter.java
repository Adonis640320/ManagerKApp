package com.sincere.kboss.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STWorkAmount;
import com.sincere.kboss.stdata.STWorkingArea;

import java.util.ArrayList;

/**
 * Created by Michael on 10/30/2016.
 */
public class WorkAmountListAdapter extends BaseAdapter {
    LayoutInflater mInflater;

    Context mContext = null;
    Activity mActivity = null;

    ArrayList<STWorkAmount> m_workamounts = null;
    public int workamount = 0;

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int pos = (Integer) buttonView.getTag();

            workamount = pos;

            notifyDataSetChanged();
        }
    };

    public WorkAmountListAdapter(Activity activity, Context context) {
        mContext = context;
        mActivity = activity;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STWorkAmount> workAmounts, int _workamount)
    {
        m_workamounts = workAmounts;

        workamount = _workamount;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_workamounts == null)
            return 0;

        return m_workamounts.size();
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
            STWorkAmount  anItem = null;

            anItem = m_workamounts.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_text_n_radio, null);
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView label = (TextView) convertView.findViewById(R.id.label);
                RadioButton radio = (RadioButton) convertView.findViewById(R.id.radio);
                TextView lblSep = (TextView) convertView.findViewById(R.id.lblSep);

                if (position == workamount) {
                    radio.setChecked(true);
                } else {
                    radio.setChecked(false);
                }
                radio.setTag(position);

                label.setText(String.valueOf(anItem.f_workamount));
                radio.setOnCheckedChangeListener(checkedChangeListener);

                if (position == m_workamounts.size()-1) {
                    lblSep.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
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
