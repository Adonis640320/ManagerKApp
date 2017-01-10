package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.worker.JobListFragment;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.03.
 */
public class JobListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STJobWorker> m_jobs = null;

    JobListFragment.ItemClickListener itemClickListener;

    public JobListAdapter(Context context, JobListFragment.ItemClickListener itemClickListener) {
        this.mctx = context;
        this.itemClickListener = itemClickListener;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STJobWorker> jobs)
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
            STJobWorker  anItem = m_jobs.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_job_worker, null);
            convertView.setTag(position);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (Integer) v.getTag();
                        STJobWorker item = m_jobs.get(pos);
                        itemClickListener.onClick(item);
                    }catch(Exception e){}
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblJobTitle = (TextView) convertView.findViewById(R.id.lblJobTitle);
                TextView lblWorkplace = (TextView) convertView.findViewById(R.id.lblWorkplace);
                TextView lblRegDate = (TextView) convertView.findViewById(R.id.lblRegDate);
                TextView lblMoney = (TextView) convertView.findViewById(R.id.lblMoney);
                TextView lblSkills = (TextView) convertView.findViewById(R.id.lblSkills);
                LinearLayout llActions01 = (LinearLayout) convertView.findViewById(R.id.llActions01);
                LinearLayout llActions02 = (LinearLayout) convertView.findViewById(R.id.llActions02);
                LinearLayout llActions03 = (LinearLayout) convertView.findViewById(R.id.llActions03);
                LinearLayout llActions04 = (LinearLayout) convertView.findViewById(R.id.llActions04);// 출근취소,작업취소,지원취소
                llActions04.setTag(position);
                final ImageView ivSupportCancel = (ImageView)convertView.findViewById(R.id.ivSupportCancel); // 지원취소
                ivSupportCancel.setTag(position);
                final ImageView ivSigninCancel = (ImageView)convertView.findViewById(R.id.ivSigninCancel); // 출근취소
                ivSigninCancel.setTag(position);

                Button btn05 = (Button) convertView.findViewById(R.id.btn05); // 출근확정
                btn05.setTag(position);
                Button btn03 = (Button) convertView.findViewById(R.id.btn03); // 근로 지원중
                btn03.setTag(position);
                Button btn01 = (Button) convertView.findViewById(R.id.btn01); // 모집중
                btn01.setTag(position);

                ivSupportCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            ivSupportCancel.setVisibility(View.GONE);
                            itemClickListener.onCancelSupport(item);
                        }catch(Exception e){}
                    }
                });

                ivSigninCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            ivSigninCancel.setVisibility(View.GONE);
                            itemClickListener.onCancelSignin(item);
                        }catch(Exception e){}
                    }
                });

                btn05.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        try {
//                            int pos = (Integer) v.getTag();
//                            STJobWorker item = m_jobs.get(pos);
//
//                            itemClickListener.onConfirmSignin(item);
//                        }catch(Exception e){}
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            itemClickListener.onClick(item);
                        }catch(Exception e){}
                    }
                });

                btn01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            itemClickListener.onClick(item);
                        }catch(Exception e){}
                    }
                });
                btn03.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            itemClickListener.onClick(item);
                        }catch(Exception e){}
                    }
                });
                llActions04.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (Integer) v.getTag();
                            STJobWorker item = m_jobs.get(pos);
                            itemClickListener.onClick(item);
                        }catch(Exception e){}
                    }
                });

                lblJobTitle.setText(anItem.f_spot_name);
                lblWorkplace.setText(anItem.job.f_address);
//                lblRegDate.setText(Functions.changeDateString(anItem.job.f_regtime));
                lblRegDate.setText(Functions.changeDateString(anItem.job.f_workdate));
                lblMoney.setText(Functions.getLocaleNumberString(anItem.job.f_payment, ""));
                lblSkills.setText(Functions.getJobsString(String.valueOf(anItem.job.f_skill), mctx));

                llActions01.setVisibility(View.INVISIBLE);
                llActions02.setVisibility(View.INVISIBLE);
                llActions03.setVisibility(View.INVISIBLE);
                llActions04.setVisibility(View.INVISIBLE);

                ivSupportCancel.setVisibility(View.GONE);
                ivSigninCancel.setVisibility(View.GONE);

                if (anItem.f_support_time.isEmpty()) {
                    llActions01.setVisibility(View.VISIBLE);
                } else {
                    if (anItem.f_support_check == 0) {
                        llActions02.setVisibility(View.VISIBLE);
                        if ( anItem.f_support_cancel==1 )
                            ivSupportCancel.setVisibility(View.GONE);
                        else
                            ivSupportCancel.setVisibility(View.VISIBLE);
                    } else {

                        llActions03.setVisibility(View.VISIBLE);
                        if (anItem.f_signin_cancel==1)
                            ivSigninCancel.setVisibility(View.GONE);
                        else
                            ivSigninCancel.setVisibility(View.VISIBLE);
                    }
                }

                if(anItem.f_support_cancel == 1 || anItem.f_signin_cancel == 1) {
                    llActions04.setVisibility(View.VISIBLE);
                    if ( anItem.f_support_cancel == 1 )
                    {
                        ((Button)convertView.findViewById(R.id.btn08)).setVisibility(View.VISIBLE);
                        ((Button)convertView.findViewById(R.id.btn06)).setVisibility(View.GONE);
                        ((Button)convertView.findViewById(R.id.btn07)).setVisibility(View.GONE);
                    }
                    else if ( anItem.f_signin_cancel == 1 )
                    {
                        ((Button)convertView.findViewById(R.id.btn06)).setVisibility(View.VISIBLE);
                        ((Button)convertView.findViewById(R.id.btn07)).setVisibility(View.GONE);
                        ((Button)convertView.findViewById(R.id.btn08)).setVisibility(View.GONE);
                    }
                }
                // llActions04 will not be shown because the cancelled job will be deleted.
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }
}
