package com.sincere.kboss.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.ConfirmWorkSubFragment;
import com.sincere.kboss.manager.DailyJobsFragment;
import com.sincere.kboss.manager.MainActivity;
import com.sincere.kboss.manager.WorkAmountActivity;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STJob;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.utils.CircularImageView;
import com.sincere.kboss.worker.JobListFragment;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.03.
 */
public class ConfirmWorkSubListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Fragment parentFrag;
    Context mctx = null;

    int m_steps = 0;

    RelativeLayout rlSelectWorkAmount;
    RelativeLayout rlElegancy;
    RelativeLayout rlSwitch, rlSwitchConfirmed;

    CircularImageView imgPhoto;
    TextView lblSkill;
    TextView lblName;
    TextView lblAge;
    TextView lblPhone;
    ToggleButton tglSignin;
    ToggleButton tglSigninConfirmed;

    ImageView imgElegancy;

    TextView lblWorkAmount;
    TextView lblGoto;

    public ArrayList<STSelectWorker> m_jobs = new ArrayList<>();
    ArrayList<STSelectWorker> m_jobs_org = null;

    public interface workamountClickListener {
        void onWorkAmountClicked(int index,int workamount_id);
    }

    workamountClickListener itemClickListener = null;

    public String getJobIds() {
        String jobids = "";
        for (int i=0; i<m_jobs.size(); i++) {
            if (jobids.isEmpty()) {
                jobids = String.valueOf(m_jobs.get(i).f_job_id);
            } else {
                jobids += "," + String.valueOf(m_jobs.get(i).f_job_id);
            }
        }

        return jobids;
    }

    public String getWorkerIds() {
        String workerids = "";
        for (int i=0; i<m_jobs.size(); i++) {
            if (workerids.isEmpty()) {
                workerids = String.valueOf(m_jobs.get(i).f_worker_id);
            } else {
                workerids += "," + String.valueOf(m_jobs.get(i).f_worker_id);
            }
        }

        return workerids;
    }

    public String getSigninStats() {
        String stats = "";
        for (int i=0; i<m_jobs.size(); i++) {
            if (stats.isEmpty()) {
                stats = String.valueOf(m_jobs.get(i).f_signin_check);
            } else {
                stats += "," + String.valueOf(m_jobs.get(i).f_signin_check);
            }
        }

        return stats;
    }

    public boolean isHalfOverPercentSignin()
    {
        int cnt = 0;
        for (int i=0; i<m_jobs.size(); i++) {
            if(m_jobs.get(i).f_signin_check == 1) cnt++;
        }
        if(cnt*2 > m_jobs.size()) return true;
        return false;
    }

    public String getElegancies() {
        String stats = "";
        for (int i=0; i<m_jobs.size(); i++) {
            if (stats.isEmpty()) {
                stats = String.valueOf(m_jobs.get(i).f_elegancy_id);
            } else {
                stats += "," + String.valueOf(m_jobs.get(i).f_elegancy_id);
            }
        }

        return stats;
    }

    public String getWorkAmounts() {
        String stats = "";
        for (int i=0; i<m_jobs.size(); i++) {
            if (stats.isEmpty()) {
                stats = String.valueOf(m_jobs.get(i).f_workamount_id);
            } else {
                stats += "," + String.valueOf(m_jobs.get(i).f_workamount_id);
            }
        }

        return stats;
    }

    public ConfirmWorkSubListAdapter(Fragment parentFrag, Context context, workamountClickListener _listener) {
        this.parentFrag = parentFrag;
        this.mctx = context;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);

        itemClickListener = _listener;
    }

    public void setData(ArrayList<STSelectWorker> jobs)
    {
        m_jobs_org = jobs;
        m_jobs.clear();
        m_jobs.addAll(m_jobs_org);
    }

    public void setSteps(int steps) {
        m_steps = steps;
        m_jobs.clear();
        switch (m_steps)
        {
            case 0:m_jobs.addAll(m_jobs_org);break;
            case 1:case 2:
                for(int i=0;i<m_jobs_org.size();i++) {
                    if(m_jobs_org.get(i).f_signin_check == 1)
                        m_jobs.add(m_jobs_org.get(i));
                }
                break;
        }
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        try
        {
            final STSelectWorker  anItem = m_jobs.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_confirm_work_sub01, null);
            convertView.setTag(position);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    STSelectWorker item = m_jobs.get(pos);
                    if(item.f_workamount_checked == 1) {
                        // TODO fragment_add_favorite
                        MainActivity mainActivity = (MainActivity)parentFrag.getActivity();
                        mainActivity.gotoAddFavoriteFragment(item);
                        //Toast.makeText(mctx,"Goto detail",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                rlSelectWorkAmount = (RelativeLayout) convertView.findViewById(R.id.rlSelectWorkAmount);
                rlElegancy = (RelativeLayout) convertView.findViewById(R.id.rlElegancy);
                rlSwitch = (RelativeLayout) convertView.findViewById(R.id.rlSwitch);
                rlSwitchConfirmed = (RelativeLayout) convertView.findViewById(R.id.rlSwitchConfirmed);

                imgPhoto = (CircularImageView) convertView.findViewById(R.id.imgPhoto);
                lblSkill = (TextView) convertView.findViewById(R.id.lblSkill);
                lblName = (TextView) convertView.findViewById(R.id.lblName);
                lblAge = (TextView) convertView.findViewById(R.id.lblAge);
                lblPhone = (TextView) convertView.findViewById(R.id.lblPhone);
                tglSignin = (ToggleButton) convertView.findViewById(R.id.tglSignin);
                tglSigninConfirmed = (ToggleButton) convertView.findViewById(R.id.tglSigninConfirmed);

                imgElegancy = (ImageView) convertView.findViewById(R.id.imgElegancy);

                lblWorkAmount = (TextView) convertView.findViewById(R.id.lblWorkAmount);
                lblGoto = (TextView) convertView.findViewById(R.id.lblGoto);

                rlSelectWorkAmount.setVisibility(View.INVISIBLE);
                rlElegancy.setVisibility(View.INVISIBLE);
                rlSwitch.setVisibility(View.INVISIBLE);
                rlSwitchConfirmed.setVisibility(View.INVISIBLE);

                switch (m_steps) {
                    case 0: {
                        if (anItem.f_signin_checked == 1) {
                            rlSwitchConfirmed.setVisibility(View.VISIBLE);
                        } else {
                            rlSwitch.setVisibility(View.VISIBLE);
                        }
                        break;
                    }

                    case 1: {
                        rlElegancy.setVisibility(View.VISIBLE);
                        break;
                    }

                    case 2: {
                        rlSelectWorkAmount.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        m_steps = 0;
                        rlSwitch.setVisibility(View.VISIBLE);
                        break;
                    }
                }

                // User info
                String photouri = anItem.f_photo;
                if (!photouri.isEmpty()) {
                    KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, imgPhoto, KbossApplication.g_imageLoaderOptions);
                } else {
                    imgPhoto.setImageResource(R.drawable.photo);
                }
                lblSkill.setText(Functions.getJobsString(String.valueOf(anItem.f_skill), mctx));
                lblName.setText(anItem.f_name);
                lblAge.setText(String.format(mctx.getString(R.string.age_templ), anItem.f_age));
                lblPhone.setText(anItem.f_mphone);

                // Signin info
                if (anItem.f_signin_checked == 1) {
                    tglSigninConfirmed.setChecked(anItem.f_signin_check==1);
                } else {
                    tglSignin.setChecked(anItem.f_signin_check==1);
                    tglSignin.setTag(position);
                    tglSignin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int pos = (Integer) buttonView.getTag();
                            STSelectWorker item = m_jobs.get(pos);
                            item.f_signin_check = isChecked ? 1 : 0;
                            for(int i=0;i<m_jobs_org.size();i++) {
                                if(m_jobs_org.get(i).f_spot_id==item.f_spot_id && m_jobs_org.get(i).f_job_id==item.f_job_id && m_jobs_org.get(i).f_worker_id==item.f_worker_id) {
                                    m_jobs_org.get(i).f_signin_check = item.f_signin_check;
                                    break;
                                }
                            }
                            if( parentFrag instanceof ConfirmWorkSubFragment) {
                                if(isHalfOverPercentSignin()) ((ConfirmWorkSubFragment)parentFrag).btnRefresh.setVisibility(View.GONE);
                                else {
                                    if(m_jobs.get(0).f_signin_checked==0) {
                                        ((ConfirmWorkSubFragment) parentFrag).llActions.setVisibility(View.VISIBLE);
                                        ((ConfirmWorkSubFragment) parentFrag).btnConfirmSignin.setVisibility(View.VISIBLE);
                                        ((ConfirmWorkSubFragment) parentFrag).btnRefresh.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }

                // Elegancy info
                switch (anItem.f_elegancy_id) {
                    case 0: {
                        imgElegancy.setImageResource(anItem.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_low : R.drawable.workamount_low);
                        break;
                    }

                    case 1: {
                        imgElegancy.setImageResource(anItem.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_medium : R.drawable.workamount_medium);
                        break;
                    }

                    case 2: {
                        imgElegancy.setImageResource(anItem.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_high : R.drawable.workamount_high);
                        break;
                    }
                }

                if (anItem.f_elegancy_checked == 0) {
                    rlElegancy.setTag(position);
                    rlElegancy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (Integer) v.getTag();
                            STSelectWorker item = m_jobs.get(pos);
                            item.f_elegancy_id = (item.f_elegancy_id+1) % 3;
                            for(int i=0;i<m_jobs_org.size();i++) {
                                if(m_jobs_org.get(i).f_spot_id==item.f_spot_id && m_jobs_org.get(i).f_job_id==item.f_job_id && m_jobs_org.get(i).f_worker_id==item.f_worker_id) {
                                    m_jobs_org.get(i).f_elegancy_id = item.f_elegancy_id;
                                    break;
                                }
                            }

                            ImageView img = (ImageView) v.findViewById(R.id.imgElegancy);

                            switch (item.f_elegancy_id) {
                                case 0: {
                                    img.setImageResource(item.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_low : R.drawable.workamount_low);
                                    break;
                                }

                                case 1: {
                                    img.setImageResource(item.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_medium : R.drawable.workamount_medium);
                                    break;
                                }

                                case 2: {
                                    img.setImageResource(item.f_elegancy_checked == 1 ? R.drawable.workamount_confirmed_high : R.drawable.workamount_high);
                                    break;
                                }
                            }
                        }
                    });
                }

                // Workamount info
                lblGoto.setVisibility(anItem.f_workamount_checked == 0 ? View.VISIBLE : View.INVISIBLE);
//                if(anItem.f_workamount_id == -1) {
//                    lblWorkAmount.setText("선택");
//                } else {
//                    lblWorkAmount.setText(String.valueOf(KbossApplication.g_workamounts.get(anItem.f_workamount_id).f_workamount));
//                }
                lblWorkAmount.setText(String.valueOf(KbossApplication.g_workamounts.get(anItem.f_workamount_id).f_workamount));

                if (anItem.f_workamount_checked == 0) {
                    rlSelectWorkAmount.setTag(position);
                    rlSelectWorkAmount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if(anItem.f_workamount_id == -1) {
//                                Toast.makeText(parentFrag.getActivity(),"공수를 선택해주세요",Toast.LENGTH_SHORT ).show();
//                                return;
//                            }
                            int pos = (Integer) v.getTag();

                            if (itemClickListener != null) {
                                itemClickListener.onWorkAmountClicked(pos,anItem.f_workamount_id);
                            }
                        }
                    });
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
