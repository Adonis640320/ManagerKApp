package com.sincere.kboss.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.manager.AddManagerActivity;
import com.sincere.kboss.manager.FavoriteFragment;
import com.sincere.kboss.manager.RegisterRequestFragment;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STContact;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STFavoriteWorker;
import com.sincere.kboss.utils.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class AddManagerListAdapter extends ArrayAdapter<AddManagerListAdapter.Item> implements PinnedSectionListView.PinnedSectionListAdapter {
    public static final int ACTION_ADD_MANAGER = 1;
    public static final int ACTION_RECOMMEND_KBOSS = 2;

    static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String name;
        public final String mphone;
        public final boolean is_member;

        public int sectionPosition;
        public int listPosition;

        public int position;

        public Item(int type, String name, String mphone, boolean is_member) {
            this.type = type;
            this.name = name;
            this.mphone = mphone;
            this.is_member = is_member;
        }

        @Override public String toString() {
            return name;
        }
    }

    LayoutInflater mInflater;
    Context mctx = null;

    AddManagerActivity.OnItemClickListener itemClickListener = null;

    public AddManagerListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        mctx = context;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    protected void prepareSections(int sectionsNumber) { }
    protected void onSectionAdded(Item section, int sectionPosition) { }

    ArrayList<STFavoriteWorker> favoriteWorkers = new ArrayList<>();
    ArrayList<STContact> contacts = new ArrayList<>();
    public void generateDataset(ArrayList<STFavoriteWorker> favoriteWorkers, ArrayList<STContact> contacts, boolean clear) {

        if (clear) clear();

        final int sectionsNumber = 2;
        prepareSections(sectionsNumber);

        int sectionPosition = 0, listPosition = 0;

        {
            Item section = new Item(Item.SECTION, mctx.getString(R.string.favorite_worker), "", false);
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;
            onSectionAdded(section, sectionPosition);
            add(section);

            for (int i=0; i<favoriteWorkers.size(); i++) {
                STFavoriteWorker worker = favoriteWorkers.get(i);
                Item item = new Item(Item.ITEM, worker.f_name, worker.f_mphone, true);
                item.sectionPosition = sectionPosition;
                item.listPosition = listPosition++;
                item.position = i;
                add(item);
            }
            this.favoriteWorkers = favoriteWorkers;
            sectionPosition++;
        }

        {
            Item section = new Item(Item.SECTION, mctx.getString(R.string.my_contacts), "", false);
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;
            onSectionAdded(section, sectionPosition);
            add(section);

            for (int i=0; i<contacts.size(); i++) {
                STContact contact = contacts.get(i);
                Item item = new Item(Item.ITEM, contact.f_name, contact.f_phone, contact.f_is_member);
                item.sectionPosition = sectionPosition;
                item.listPosition = listPosition++;
                item.position = i;
                add(item);
            }
            this.contacts = contacts;
            sectionPosition++;
        }
    }

    public void setItemClickListener(AddManagerActivity.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    /**
     * Make a view to hold each row.
     *
     * @see ListAdapter#getView(int, View,
     *      ViewGroup)
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

                //TextView lblName = (TextView) view.findViewById(R.id.lblName);
                TextView lblAction = (TextView) view.findViewById(R.id.lblAction);
                TextView tvArrow = (TextView) view.findViewById(R.id.tvArrow);
                switch (item.type) {
                    case Item.SECTION:
                        lblAction.setText("");
                        view.setBackgroundColor(mctx.getResources().getColor(R.color.clr_gray_light));
                        tvArrow.setVisibility(View.INVISIBLE);
                        break;

                    case Item.ITEM:
                        //lblName.setText(item.name);
                        tvArrow.setVisibility(View.VISIBLE);
                        if (item.is_member) {
                            lblAction.setTextColor(mctx.getResources().getColor(R.color.clr_blue_light));
                            lblAction.setText(mctx.getString(R.string.add_manager));
                            if(favoriteWorkers.get(item.position).f_is_manager == 0) {
                                lblAction.setVisibility(View.VISIBLE);
                                tvArrow.setVisibility(View.VISIBLE);
                            } else {
                                lblAction.setVisibility(View.INVISIBLE);
                                tvArrow.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            lblAction.setTextColor(mctx.getResources().getColor(R.color.clr_red_dark));
                            lblAction.setText(mctx.getString(R.string.recommend_kboss));
                            lblAction.setVisibility(View.VISIBLE);
                            tvArrow.setVisibility(View.VISIBLE);
                        }

                        lblAction.setTag(position);
                        lblAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos =(Integer) v.getTag();
                                Item item1 = getItem(pos);
                                if (item1.is_member) {
                                    itemClickListener.onAddManagerClick(item1.mphone,item1.position);
                                } else {
                                    itemClickListener.onRecommendClick(item1.mphone,item1.position);
                                }
                            }
                        });

                        break;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return view;
    }
}
