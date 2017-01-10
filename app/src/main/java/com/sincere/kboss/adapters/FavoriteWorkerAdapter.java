package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.FavoriteFragment;
import com.sincere.kboss.manager.RegisterRequestFragment;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.utils.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class FavoriteWorkerAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STFavoriteWorker> m_workers = null;

    FavoriteFragment.OnRemoveClickListener removeClickListener;

    public FavoriteWorkerAdapter(Context context, FavoriteFragment.OnRemoveClickListener removeClickListener1) {
        mctx = context;
        removeClickListener = removeClickListener1;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STFavoriteWorker> workers)
    {
        m_workers = workers;
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
            STFavoriteWorker anItem = m_workers.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_favorite, null);
            //}

            if (KbossApplication.UI_TEST == false) {
                CircularImageView imgPhoto = (CircularImageView) convertView.findViewById(R.id.imgPhoto);
                TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
                TextView lblRegDate = (TextView) convertView.findViewById(R.id.lblRegDate);
                TextView lblSpotName = (TextView) convertView.findViewById(R.id.lblSpotName);

                RelativeLayout rlRemove = (RelativeLayout) convertView.findViewById(R.id.rlRemove);
                rlRemove.setTag(position);
                rlRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (Integer) v.getTag();
                        removeClickListener.onRemove(pos, m_workers.get(pos));
                    }
                });

                if (!anItem.f_photo.isEmpty()) {
                    KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + anItem.f_photo, imgPhoto, KbossApplication.g_imageLoaderOptions);
                }
                lblName.setText(anItem.f_name);
                lblRegDate.setText(Functions.getDateStringFmt(anItem.f_regtime, "%d.%02d.%02d"));
                lblSpotName.setText(anItem.f_spot);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }
}
