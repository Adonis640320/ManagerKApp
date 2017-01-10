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
import com.sincere.kboss.manager.RegisterRequestFragment;
import com.sincere.kboss.stdata.STPoint;
import com.sincere.kboss.stdata.STCompany;
import com.sincere.kboss.stdata.STSpotNotReady;

import java.util.ArrayList;

/**
 * Created by Michael on 11/7/2016.
 */
public class CompanyListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mctx = null;

    ArrayList<STCompany> m_companies = null;
    RegisterRequestFragment.OnCompanyClickListener companyClickListener;

    public CompanyListAdapter(Context context, RegisterRequestFragment.OnCompanyClickListener companyClickListener) {
        mctx = context;
        this.companyClickListener = companyClickListener;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<STCompany> companies)
    {
        m_companies = companies;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_companies == null)
            return 0;

        return m_companies.size();
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
            STCompany anItem = m_companies.get(position);

            //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_company, null);
            //}
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    STCompany clickedCompany = m_companies.get(pos);
                    companyClickListener.onClick(clickedCompany);
                }
            });

            if (KbossApplication.UI_TEST == false) {
                TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
                TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);

                if(anItem.f_feedback.isEmpty()) lblName.setText(anItem.f_name);
                else lblName.setText(anItem.f_name+"(1)");
                lblDate.setText(Functions.getDateStringFmt(anItem.f_regtime, "%d.%02d.%02d"));


            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return convertView;
    }
}
