package com.sincere.kboss.manager;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.utils.PagerSlidingTabStrip;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Michael on 11/2/2016.
 */
public class ConfirmWorkFragment extends FragmentTempl {
    private ConfirmSectionsPagerAdapter mSectionsPagerAdapter;

    TextView lblTitle;
    TabLayout tabLayout;
    //PagerSlidingTabStrip tabs;
    ViewPager viewPager;

    public static int curFrag = 0;
//    public static ConfirmWorkSubFragment selConfirmWorkSubFragment = null;
    public static SparseArray<ConfirmWorkSubFragment> registeredFragments = new SparseArray<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_work, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("test","OnResume ConfirmWorkFragment updateChildFragments");
        //updateChildFragments();
    }

    void initUI(View v) {
        tabLayout = (TabLayout) v.findViewById(R.id.tabs02);
        //tabs = (PagerSlidingTabStrip)v.findViewById(R.id.tabs);

        mSectionsPagerAdapter = new ConfirmSectionsPagerAdapter(getChildFragmentManager());
        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        viewPager = (ViewPager) v.findViewById(R.id.container02);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("test","Confirm - onPageScrolled "+position);
            }

            @Override
            public void onPageSelected(int position) {
                curFrag = position;
//                selConfirmWorkSubFragment = mSectionsPagerAdapter.getFragment(position);
                Log.e("test","Confirm - onPageSelected "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        tabs.setViewPager(viewPager);
//        tabs.setTextSize(20);
        viewPager.setCurrentItem(28);

//        Log.e("test","OnCreate ConfirmWorkFragment updateChildFragments");
//        updateTitle();
//        updateChildFragments();

    }

    public void updateTitle() {
        if (MainActivity.g_curSpot < 0) {
            lblTitle.setText("");
        } else {
            String temp = MainActivity.g_spots.get(MainActivity.g_curSpot).f_name;
            if(temp.length()>15) {
                temp = temp.substring(0,15)+"...";
            }
            lblTitle.setText(temp);
        }
    }

    void updateChildFragments() {
        Log.e("test1","confirm updateChildFragments");
        // refresh child fragments
        int spot_id;
        if (MainActivity.g_curSpot < 0) {
            spot_id = 0;
        } else {
            spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
        }
        int st = curFrag-1,ed = curFrag+2;
        if(curFrag<1) {st = 0; ed = 3;}
        if(curFrag>56) { st = 54;  ed = 57;}
        for (int i=st; i<ed; i++) {
            ConfirmWorkSubFragment frag = mSectionsPagerAdapter.getFragment(i);
            if (frag == null) {
                mSectionsPagerAdapter.instantiateItem(viewPager, i);
//                continue;
            } else {
                frag.updateHistoryList(Functions.getDateTimeStringFromToday(i-28), spot_id);
            }
        }
    }

    public class ConfirmSectionsPagerAdapter extends FragmentPagerAdapter {

        public ConfirmSectionsPagerAdapter(FragmentManager fm) { super(fm);}

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            int spot_id;

            if (MainActivity.g_curSpot < 0) {
                spot_id = 0;
            } else {
                spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
            }

            ConfirmWorkSubFragment newfrag = new ConfirmWorkSubFragment();
            Bundle args = new Bundle();
            args.putString(ConfirmWorkSubFragment.ARG_DATE, Functions.getDateTimeStringFromToday(position-28));
            args.putInt(ConfirmWorkSubFragment.ARG_SPOT_ID, spot_id);
            newfrag.setArguments(args);

            registeredFragments.put(position, newfrag);

            return newfrag;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 57;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position<57) {
                return Functions.getDateStringWeekday(position - 28);
            }
//            switch (position) {
//                case 0:
//                    return Functions.getDateStringWeekday(0);
//                case 1:
//                    return Functions.getDateStringWeekday(1);
//                case 2:
//                    return Functions.getDateStringWeekday(2);
//                case 3:
//                    return Functions.getDateStringWeekday(3);
//
//            }
            return null;
        }

        public ConfirmWorkSubFragment getFragment(int position) {
            return registeredFragments.get(position);
        }

    }
}
