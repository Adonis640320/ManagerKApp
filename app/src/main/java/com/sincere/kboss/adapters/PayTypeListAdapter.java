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
import com.sincere.kboss.PayTypeActivity;
import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STPayType;
import com.sincere.kboss.stdata.STWorkingArea;

import java.util.ArrayList;

/**
 * Created by Michael on 10/30/2016.
 */
public class PayTypeListAdapter extends BaseAdapter {
    LayoutInflater mInflater;

    Context mContext = null;
    Activity mActivity = null;

    ArrayList<STPayType> m_paytypes = null;
    int paytype = 0;

    PayTypeActivity.ItemClickListener itemClickListener;

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int pos = (Integer) buttonView.getTag();

            paytype = pos+1;
            itemClickListener.onClick(paytype);
//
            notifyDataSetChanged();
        }
    };

    public void setPayType(int paytype)
    {
        this.paytype = paytype;
        notifyDataSetChanged();
    }

    public PayTypeListAdapter(Activity activity, Context context, PayTypeActivity.ItemClickListener itemClickListener) {
        mContext = context;
        mActivity = activity;

        this.itemClickListener = itemClickListener;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STPayType> payTypes)
    {
        m_paytypes = payTypes;

        paytype = KbossApplication.g_userinfo.f_pay_type;
        if (paytype == 0) {
            paytype = 1;
        }
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_paytypes == null)
            return 0;

        return m_paytypes.size();
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
            STPayType  anItem = null;

            anItem = m_paytypes.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_text_n_radio_n_comment, null);
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (Integer) view.getTag();

                    paytype = pos+1;
                    itemClickListener.onClick(paytype);
//
                    notifyDataSetChanged();
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView label1 = (TextView) convertView.findViewById(R.id.label1);
                TextView label2 = (TextView) convertView.findViewById(R.id.label2);
                RadioButton radio = (RadioButton) convertView.findViewById(R.id.radio);
                TextView lblSep = (TextView) convertView.findViewById(R.id.lblSep);

                if (position == paytype-1) {
                    radio.setChecked(true);
                } else {
                    radio.setChecked(false);
                }
                radio.setTag(position);

                label1.setText(anItem.f_name);
                label2.setText(anItem.f_subname);
                radio.setOnCheckedChangeListener(checkedChangeListener);

                if (position == m_paytypes.size()-1) {
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

    public String getPayTypeString() {
        if (paytype == 0) {
            return "";
        }

        return String.valueOf(paytype);
    }
}
