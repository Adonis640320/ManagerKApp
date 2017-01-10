package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.DailyJobsFragment;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STJobManager;
import com.sincere.kboss.worker.JobListFragment;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.03.
 */
public class DailyJobListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STJobManager> m_jobs = null;

    DailyJobsFragment.ItemClickListener itemClickListener;

    public DailyJobListAdapter(Context context, DailyJobsFragment.ItemClickListener itemClickListener) {
        this.mctx = context;
        this.itemClickListener = itemClickListener;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STJobManager> jobs)
    {
        m_jobs = jobs;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_jobs == null)
            return 0;

        return m_jobs.size();
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
            STJobManager  anItem = m_jobs.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_job_manager, null);
            convertView.setTag(position);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    STJobManager item = m_jobs.get(pos);

                    itemClickListener.onClick(item);
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblSkill = (TextView) convertView.findViewById(R.id.lblSkill);
                TextView lblWorkerCount = (TextView) convertView.findViewById(R.id.lblWorkerCount);
                TextView lblStartTime = (TextView) convertView.findViewById(R.id.lblStartTime);
                TextView lblMoney = (TextView) convertView.findViewById(R.id.lblMoney);
                RelativeLayout rlStat01 = (RelativeLayout) convertView.findViewById(R.id.rlStat01);
                RelativeLayout rlStat02 = (RelativeLayout) convertView.findViewById(R.id.rlStat02);
                RelativeLayout rlStat03 = (RelativeLayout) convertView.findViewById(R.id.rlStat03);

                lblSkill.setText(Functions.getJobsString(String.valueOf(anItem.job.f_skill), mctx));
                lblWorkerCount.setText(String.format(mctx.getString(R.string.worker_count_templ), anItem.job.f_worker_count));
                lblStartTime.setText(anItem.job.f_worktime_start.substring(0,5));
                lblMoney.setText(Functions.getLocaleNumberString(anItem.job.f_payment, ""));

                rlStat01.setVisibility(View.INVISIBLE);
                rlStat02.setVisibility(View.INVISIBLE);
                rlStat03.setVisibility(View.INVISIBLE);
                if (anItem.f_support_count == 0) {
                    rlStat01.setVisibility(View.VISIBLE);
                } else if (anItem.f_support_check_count == 0) {
                    rlStat02.setVisibility(View.VISIBLE);
                    TextView lbl03 = (TextView) convertView.findViewById(R.id.lbl03);
                    if (anItem.f_support_count == 0) {
                        lbl03.setText("지원자: 없음");
                    } else {
                        lbl03.setText(String.format(mctx.getString(R.string.support_count), anItem.f_support_count));
                    }
                } else {
                    rlStat03.setVisibility(View.VISIBLE);
                    TextView lbl05 = (TextView) convertView.findViewById(R.id.lbl05);
                    TextView lbl06 = (TextView) convertView.findViewById(R.id.lbl06);
                    if (anItem.f_support_check_count == 0) {
                        lbl05.setText("근로자: 없음");
                    } else {
                        lbl05.setText(String.format(mctx.getString(R.string.worker_count), anItem.f_support_check_count-anItem.f_support_cancel_count));
                    }

                    if (anItem.f_support_cancel_count == 0) {
                        lbl06.setText("취소자: 없음");
                    } else {
                        lbl06.setText(String.format(mctx.getString(R.string.cancel_count), anItem.f_support_cancel_count));
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
