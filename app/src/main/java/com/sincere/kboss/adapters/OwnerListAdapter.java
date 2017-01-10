package com.sincere.kboss.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hb.views.PinnedSectionListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.AddManagerActivity;
import com.sincere.kboss.manager.FavoriteFragment;
import com.sincere.kboss.manager.RegisterRequestFragment;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STContact;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STOwner;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STUserInfo;
import com.sincere.kboss.utils.CircularImageView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class OwnerListAdapter extends ArrayAdapter<OwnerListAdapter.Item> {

    static class Item {

        public static final int ITEM = 0;

        public int id;
        public String photo;
        public String name;
        public String mphone;
        public int spot_id;
        public boolean is_connected;
        public boolean is_i_created;

        public int listPosition;

        public Item(int id, String photo, String name, String mphone, int spot_id,  boolean is_connected, boolean is_i_created) {
            this.id = id;
            this.photo = photo;
            this.name = name;
            this.mphone = mphone;
            this.spot_id = spot_id;
            this.is_connected = is_connected;
            this.is_i_created = is_i_created;
        }

        @Override public String toString() {
            return name;
        }
    }

    LayoutInflater mInflater;
    Context mctx = null;

    public OwnerListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        mctx = context;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    protected void prepareSections(int sectionsNumber) { }
    protected void onSectionAdded(Item section, int sectionPosition) { }

    public void generateDataset(ArrayList<STOwner> owners, boolean clear) {

        if (clear) clear();

        int listPosition = 0;

        for (int i=0; i<owners.size(); i++) {
            STOwner owner = owners.get(i);
            Item item = new Item(owner.f_id, owner.f_photo, owner.f_name, owner.f_mphone, owner.f_spot_id, owner.f_connected==1, owner.f_recommend_id==KbossApplication.g_userinfo.f_id);
            item.listPosition = listPosition++;
            add(item);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return Item.ITEM;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) super.getView(position, convertView, parent);
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.

        try
        {
            if (KbossApplication.UI_TEST == false) {
                Item item = getItem(position);

                CircularImageView imgPhoto = (CircularImageView) view.findViewById(R.id.imgPhoto);
                TextView lblPhone = (TextView) view.findViewById(R.id.lblPhone);
                ToggleButton tglSignin = (ToggleButton) view.findViewById(R.id.tglSignin);

                lblPhone.setText(item.mphone);
                KbossApplication.g_imageLoader.displayImage(ServiceParams.assetsBaseUrl + item.photo, imgPhoto, KbossApplication.g_imageLoaderOptions);
                tglSignin.setVisibility(item.is_i_created ? View.VISIBLE : View.INVISIBLE);
                tglSignin.setChecked(item.is_connected);
                tglSignin.setTag(position);
                tglSignin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (Integer) buttonView.getTag();
                        Item _item = getItem(pos);

                        _item.is_connected = isChecked;

                        callApiConnectSpot(_item.spot_id, isChecked?1:0);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return view;
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
