package com.sincere.kboss.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STMonth;
import com.sincere.kboss.stdata.STNotice;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SunMS on 12/29/2016.
 */

public class YearMonthDialog extends CommonDialog implements View.OnClickListener{
    private ClickListner mListner;
    Context mcontext;
    MonthAdapter m_adpmonth;
    Date today=new Date();
    int cur_year;
    int prevmonth;
    TextView yeartext;
    String[] monthname={"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};
    ArrayList<STMonth> arrMonth=new ArrayList<>();
    public interface ClickListner{
        void okClick(int year,int monthOfYear);
        void onBack();
    }
    public YearMonthDialog(Context context, YearMonthDialog.ClickListner listner){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mListner=listner;
        mcontext=context;
    }
    @Override
    public void onClick(View v) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_year_month);
        prevmonth=-1;
        cur_year=today.getYear()+1900;
        ((ImageView)findViewById(R.id.id_previous_action)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_year--;
                yeartext.setText(String.valueOf(cur_year));
            }
        });
        ((ImageView)findViewById(R.id.id_next_action)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_year++;
                yeartext.setText(String.valueOf(cur_year));
            }
        });
        yeartext=(TextView)findViewById(R.id.id_year_name);
        yeartext.setText(String.valueOf(today.getYear()+1900));
        ((ImageView)findViewById(R.id.id_close_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( prevmonth!=-1 )
                    mListner.okClick(cur_year,prevmonth+1);
                dismiss();
            }
        });
        final GridView gridView = (GridView) findViewById(R.id.gv_month);
        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_UP)
                    gridView.requestDisallowInterceptTouchEvent(false);
                else {
                    gridView.requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });
        m_adpmonth = new MonthAdapter();
        gridView.setAdapter(m_adpmonth);
        Log.e("Dialog_Year:",String.valueOf(today.getYear()+1900));
        for ( int i=0;i<12;i++ )
        {
            STMonth tmonth=new STMonth();
            tmonth.isSelect=false;
            tmonth.monthname=monthname[i];
            m_adpmonth.add(tmonth);
        }
        m_adpmonth.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final STMonth item = m_adpmonth.getItem(i);

                if ( prevmonth!=-1 )
                {
                    STMonth tmonth=m_adpmonth.getItem(prevmonth);
                    tmonth.isSelect=!tmonth.isSelect;
                }
                item.isSelect=!item.isSelect;
                prevmonth=i;
                m_adpmonth.notifyDataSetChanged();

            }
        });

    }
    class MonthAdapter extends ArrayAdapter<STMonth>{
        LayoutInflater m_inflater;
        public MonthAdapter() {
            super(mcontext, 0, new ArrayList<STMonth>());
            m_inflater = (LayoutInflater) mcontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = m_inflater.inflate(R.layout.item_types_of_month, parent, false);
            }
            final STMonth item = getItem(position);
            TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);


            tvContent.setSelected(item.isSelect);

            if ( item.isSelect )
            {
                tvContent.setBackgroundColor(Color.DKGRAY);
                tvContent.setTextColor(Color.WHITE);
            }
            else
            {
                tvContent.setBackgroundColor(Color.WHITE);
                tvContent.setTextColor(Color.BLACK);
            }
            if ( today.getMonth()==position )
            {
                tvContent.setTextColor(Color.RED);
            }
            tvContent.setText(item.monthname);
            return convertView;
        }
    }
}
