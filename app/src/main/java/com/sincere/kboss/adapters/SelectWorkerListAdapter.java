package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.SelectWorkerActivity;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.utils.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class SelectWorkerListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STSelectWorker> m_workers = null;

    public ArrayList<Boolean> m_chkStat = new ArrayList<>();

    SelectWorkerActivity.NotifySelectChanged notifySelectChanged = null;

    public SelectWorkerListAdapter(Context context, SelectWorkerActivity.NotifySelectChanged notifySelectChanged2) {
        mctx = context;
        notifySelectChanged = notifySelectChanged2;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STSelectWorker> spots)
    {
        m_workers = spots;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_workers == null)
            return 0;

        return m_workers.size();
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

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (Integer) v.getTag();
            STSelectWorker anItem = m_workers.get(pos);

            if (anItem.f_support_check == 0 ) {
                boolean selstat = m_chkStat.get(pos);
                m_chkStat.set(pos, !selstat);

                notifyDataSetChanged();
            }

            if (notifySelectChanged != null) {
                notifySelectChanged.onSelectChanged();
            }
        }
    };

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
            STSelectWorker anItem = m_workers.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_select_worker, null);
            convertView.setTag(position);
            convertView.setOnClickListener(itemClickListener);
            //}

            if (KbossApplication.UI_TEST == false) {
                CircularImageView imgPhoto = (CircularImageView) convertView.findViewById(R.id.imgPhoto);
                imgPhoto.setTag(position);
                imgPhoto.setOnClickListener(itemClickListener);

                TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
                TextView lblAgeHistory = (TextView) convertView.findViewById(R.id.lblAgeHistory);
                TextView lblElegancy = (TextView) convertView.findViewById(R.id.lblElegancy);
                //TextView lbl02 = (TextView) convertView.findViewById(R.id.lbl02);
                ImageView ivStar1 = (ImageView)convertView.findViewById(R.id.ivStar1);
                ImageView ivStar2 = (ImageView)convertView.findViewById(R.id.ivStar2);
                ImageView ivStar3 = (ImageView)convertView.findViewById(R.id.ivStar3);
                ImageView ivStar4 = (ImageView)convertView.findViewById(R.id.ivStar4);
                ImageView ivStar5 = (ImageView)convertView.findViewById(R.id.ivStar5);
                LinearLayout llHistory = (LinearLayout) convertView.findViewById(R.id.llHistory);
                LinearLayout llRelate1 = (LinearLayout) convertView.findViewById(R.id.llRelate1);
                llRelate1.setVisibility(View.GONE);
                ImageView ivRelate1 = (ImageView) convertView.findViewById(R.id.ivRelate1);
                TextView tvRelate1 = (TextView) convertView.findViewById(R.id.tvRelate1);
                LinearLayout llRelate2 = (LinearLayout) convertView.findViewById(R.id.llRelate2);
                llRelate2.setVisibility(View.GONE);
                ImageView ivRelate2 = (ImageView) convertView.findViewById(R.id.ivRelate2);
                TextView tvRelate2 = (TextView) convertView.findViewById(R.id.tvRelate2);
                View space = (View) convertView.findViewById(R.id.space);
                space.setVisibility(View.GONE);
                llHistory.setVisibility(View.GONE);

                if (!anItem.f_together_workdate.isEmpty())
                {
                    llHistory.setVisibility(View.VISIBLE);
                    llRelate1.setVisibility(View.VISIBLE);
                    ivRelate1.setImageResource(R.drawable.flag_blue);
                    tvRelate1.setText(Functions.changeDateString(anItem.f_together_workdate)+"에 함께 일했던 근로자입니다.");
                    tvRelate1.setTextColor(mctx.getResources().getColor(R.color.clr_blue_dark));
                }
                if (anItem.f_is_favorite != 0) {
                    if(!anItem.f_together_workdate.isEmpty()) space.setVisibility(View.VISIBLE);
                    llHistory.setVisibility(View.VISIBLE);
                    llRelate2.setVisibility(View.VISIBLE);
                    ivRelate2.setImageResource(R.drawable.flag_blue);
                    tvRelate2.setText(mctx.getResources().getText(R.string.selectworker_relatestatus_want));
                    tvRelate2.setTextColor(mctx.getResources().getColor(R.color.clr_blue_dark));
                }

                boolean selstat = m_chkStat.get(position);
                if (selstat) {
                    if (anItem.f_support_cancel==1 || anItem.f_signin_cancel==1) {
                        imgPhoto.setImageResource(R.drawable.minus_round_red);
                        lblName.setTextColor(mctx.getResources().getColor(R.color.clr_red_dark));
                        lblAgeHistory.setTextColor(mctx.getResources().getColor(R.color.clr_red_dark));
                        llHistory.setVisibility(View.VISIBLE);
                        llRelate1.setVisibility(View.VISIBLE);
                        space.setVisibility(View.GONE);
                        llRelate2.setVisibility(View.GONE);
                        ivRelate1.setImageResource(R.drawable.flag_red);
                        tvRelate1.setText(mctx.getResources().getText(R.string.selectworker_relatestatus_cancel));
                        tvRelate1.setTextColor(mctx.getResources().getColor(R.color.clr_red_dark));
                    } else {
                        imgPhoto.setImageResource(R.drawable.check_blue_round);
                        lblName.setTextColor(mctx.getResources().getColor(R.color.clr_blue_light));
                        lblAgeHistory.setTextColor(mctx.getResources().getColor(R.color.clr_blue_light));
                    }
                } else {
                    String photouri = anItem.f_photo;
                    if (!photouri.isEmpty()) {
                        KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + photouri, imgPhoto, KbossApplication.g_imageLoaderOptions);
                    } else {
                        imgPhoto.setImageResource(R.drawable.photo);
                    }
                    lblName.setTextColor(mctx.getResources().getColor(android.R.color.black));
                    lblAgeHistory.setTextColor(mctx.getResources().getColor(android.R.color.black));
                }



                lblName.setText(anItem.f_name);
                lblAgeHistory.setText(anItem.getYearsString());
                lblElegancy.setText(String.valueOf(anItem.f_ratings));
                switch(anItem.f_ratings) {
                    case 80:
                        ivStar5.setImageResource(R.drawable.star_unselected);
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_selected);
                        ivStar4.setImageResource(R.drawable.star_selected);break;
                    case 60:
                        ivStar5.setImageResource(R.drawable.star_unselected);
                        ivStar4.setImageResource(R.drawable.star_unselected);
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);
                        ivStar3.setImageResource(R.drawable.star_selected);break;
                    case 40:
                        ivStar5.setImageResource(R.drawable.star_unselected);
                        ivStar4.setImageResource(R.drawable.star_unselected);
                        ivStar3.setImageResource(R.drawable.star_unselected);
                        ivStar1.setImageResource(R.drawable.star_selected);
                        ivStar2.setImageResource(R.drawable.star_selected);break;
                    case 20:
                        ivStar5.setImageResource(R.drawable.star_unselected);
                        ivStar4.setImageResource(R.drawable.star_unselected);
                        ivStar3.setImageResource(R.drawable.star_unselected);
                        ivStar2.setImageResource(R.drawable.star_unselected);
                        ivStar1.setImageResource(R.drawable.star_selected); break;
                    case 0:
                        ivStar5.setImageResource(R.drawable.star_unselected);
                        ivStar4.setImageResource(R.drawable.star_unselected);
                        ivStar3.setImageResource(R.drawable.star_unselected);
                        ivStar1.setImageResource(R.drawable.star_unselected); break;
                }
                //lbl02.setText(anItem.getRatingsString());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return convertView;
    }
}
