package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.LinkSpotFragment;
import com.sincere.kboss.manager.RegisterRequestFragment;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STSpot;
import com.sincere.kboss.stdata.STSpotNotReady;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class LinkSpotListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    LinkSpotFragment.OnSpotClickListener spotClickListener = null;

    ArrayList<STSpot> m_spots = null;

    public LinkSpotListAdapter(Context context, LinkSpotFragment.OnSpotClickListener spotClickListener1) {
        mctx = context;
        spotClickListener = spotClickListener1;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STSpot> spots)
    {
        m_spots = spots;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_spots == null)
            return 0;

        return m_spots.size();
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
            STSpot anItem = m_spots.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_link_spot, null);
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    STSpot clickedSpot = m_spots.get(pos);
                    spotClickListener.onClick(clickedSpot);
                }
            });
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
                TextView lblStat = (TextView) convertView.findViewById(R.id.lblStat);
                ToggleButton tglLinkSpot = (ToggleButton) convertView.findViewById(R.id.tglLinkSpot);

                String temp = anItem.f_name;
                if(temp.length()>15) {
                    temp = temp.substring(0,15)+"...";
                }
                lblName.setText(temp);

                if (anItem.f_valid == 1) {
                    lblStat.setVisibility(View.INVISIBLE);
                    tglLinkSpot.setVisibility(View.VISIBLE);
                } else {
                    lblStat.setVisibility(View.VISIBLE);
                    tglLinkSpot.setVisibility(View.INVISIBLE);
                }

                tglLinkSpot.setChecked(anItem.f_connected == 1);
                tglLinkSpot.setTag(position);
                tglLinkSpot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (Integer) buttonView.getTag();
                        callApiConnectSpot(m_spots.get(pos).f_id, isChecked?1:0);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }

    void callApiConnectSpot(int spot_id, int connected) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }
        };

        ServiceManager.inst.connectSpot(spot_id, connected, handler);
    }
}
