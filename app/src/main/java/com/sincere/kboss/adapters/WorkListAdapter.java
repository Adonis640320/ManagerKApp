package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.worker.WorkListFragment;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.03.
 */
public class WorkListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STJobWorker> m_histories = null;

    WorkListFragment.ItemClickListener itemClickListener;

    public WorkListAdapter(Context context, WorkListFragment.ItemClickListener itemClickListener) {
        this.mctx = context;
        this.itemClickListener = itemClickListener;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STJobWorker> histories)
    {
        m_histories = histories;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_histories == null)
            return 0;

        return m_histories.size();
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
            STJobWorker  anItem = m_histories.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_workhistory, null);
            convertView.setTag(position);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    STJobWorker item = m_histories.get(pos);

                    itemClickListener.onClick(item);
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                ImageView imgStat = (ImageView) convertView.findViewById(R.id.imgStat);
                TextView lblMoney = (TextView) convertView.findViewById(R.id.lblMoney);
                TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
                TextView lblWorkplace = (TextView) convertView.findViewById(R.id.lblWorkplace);
                Button btnStat = (Button) convertView.findViewById(R.id.btnStat);

                lblMoney.setText(Functions.getLocaleNumberString(anItem.job.f_payment, ""));
                lblDate.setText(Functions.getDateString(anItem.job.f_workdate));
                lblWorkplace.setText(anItem.f_spot_name);

                if (anItem.f_signout_check == 1) {
                    imgStat.setImageResource(R.drawable.pay_done);
                    btnStat.setText(Functions.getLocaleNumberString(anItem.job.f_payment, mctx.getString(R.string.jobhistory_payfinish)));
                    btnStat.setBackgroundColor(mctx.getResources().getColor(R.color.clr_gray_02));
                } else {
//                    if (anItem.f_signout_time.isEmpty() || anItem.f_signout_time.equals("0000-00-00 00:00:00")) {
//
//                    }

                    if(anItem.f_workamount_checked != 1) {
                        btnStat.setText(R.string.jobhistory_notverify);
                        btnStat.setBackgroundColor(mctx.getResources().getColor(R.color.clr_yellow_dark));
                        imgStat.setImageResource(R.drawable.signin_done);
                    } else {
                        imgStat.setImageResource(R.drawable.work_done);
                        btnStat.setText(Functions.getLocaleNumberString(anItem.job.f_payment, mctx.getString(R.string.jobhistory_payplan)));
                        btnStat.setBackgroundColor(mctx.getResources().getColor(R.color.clr_red_dark));
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
