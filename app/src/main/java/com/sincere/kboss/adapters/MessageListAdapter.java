package com.sincere.kboss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STPoint;

import java.util.ArrayList;

/**
 * Created by Michael on 2016.11.04.
 */
public class MessageListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<String> m_messages = null;

    public MessageListAdapter(Context context) {
        mctx = context;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<String> points)
    {
        m_messages = m_messages;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_messages == null)
            return 0;

        return m_messages.size();
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
     * @see android.widget.ListAdapter#getView(int, View,
     *      ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        try
        {
            String anItem = m_messages.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_info_message, null);
            //}

            if (KbossApplication.UI_TEST == false) {
                TextView lblMessage = (TextView) convertView.findViewById(R.id.tvMessage);
                lblMessage.setText(anItem);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }
}
