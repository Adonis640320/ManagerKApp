package com.sincere.kboss.manager;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.service.RetVal;
import com.sincere.kboss.service.ServiceManager;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.stdata.STSpot;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class ManageSpotFragment extends FragmentTempl {
    TextView lblTitle;
    Spinner spnTitle;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button btnNewJob;

    private SpotSectionsPagerAdapter mSectionsPagerAdapter;
    public static int curFrag = 0;
    //public static DailyJobsFragment selDailyJobsFragment = null;

    //public static DailyJobsFragment dailyJobsFragment1 = null,dailyJobsFragment2 = null,dailyJobsFragment3 = null,dailyJobsFragment4 = null;

    public static SparseArray<DailyJobsFragment> registeredFragments = new SparseArray<DailyJobsFragment>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_spot, container, false);

        initUI(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("test","ManageSpotFragment - OnResume");
        //updateChildFragments();
    }

    void initUI(View v) {
        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        spnTitle = (Spinner) v.findViewById(R.id.spnTitle);

        spnTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.g_curSpot = position;

                updateChildFragments();
                // When the given dropdown item is selected, show its contents in the
                // container view.
                /*getFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        mSectionsPagerAdapter = new SpotSectionsPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) v.findViewById(R.id.container);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.e("test","Manger - onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                curFrag = position;
                //selDailyJobsFragment = mSectionsPagerAdapter.getFragment(position);
                //Log.e("test","Manger - onPageSelected");
//                int spot_id;
//                if (MainActivity.g_curSpot < 0) {
//                    spot_id = 0;
//                } else {
//                    spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
//                }
//
//
//                    DailyJobsFragment frag = mSectionsPagerAdapter.getFragment(position);
//                    if (frag == null) {
//                        Log.e("test","Manger - UpdateChildFragment null "+position);
//                        mSectionsPagerAdapter.instantiateItem(viewPager, position);
//                    } else {
//                        Log.e("test","Manger - UpdateChildFragment "+position);
//                        frag.updateJobList(Functions.getDateTimeStringFromToday(position), spot_id);
//                    }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE); // added by Adonis
        viewPager.setCurrentItem(28);
//        viewPager.setCurrentItem(0);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        btnNewJob = (Button) v.findViewById(R.id.btnNewJob);
        btnNewJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNewJobActivity();
            }
        });

        callApiGetSpots();
    }

    void gotoNewJobActivity() {
        //modified by Adonis
        // 선택된 pager fragment의 타이틀 얻기

        int selectedIndex = viewPager.getCurrentItem();
        CharSequence pageTitle = mSectionsPagerAdapter.getPageTitle(selectedIndex);

        Intent i = new Intent(getActivity(), NewJobActivity.class);
        int spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
        i.putExtra(NewJobActivity.EXTRA_SPOT_ID, spot_id);
        i.putExtra(NewJobActivity.SELECTED_DATE_STR, pageTitle); // added by Adonis
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void updateChildFragments() {
        // refresh child fragments
        int spot_id;
        if (MainActivity.g_curSpot < 0) {
            spot_id = 0;
        } else {
            spot_id = MainActivity.g_spots.get(MainActivity.g_curSpot).f_id;
        }
        //modified by Adonis
        int st = curFrag-1,ed = curFrag+2;
        if(curFrag<1) {st = 0; ed = 3;}
        if(curFrag>56) { st = 54;  ed = 57;}
        for(int i = 0; i < 57; i++){
//        for (int i=st; i<ed; i++) {
            DailyJobsFragment frag = mSectionsPagerAdapter.getFragment(i);
            if (frag == null) {
                mSectionsPagerAdapter.instantiateItem(viewPager, i);
            } else {
                frag.updateJobList(Functions.getDateTimeStringFromToday(i - 28), spot_id);
            }
        }
    }

    void callApiGetSpots() {

        handler = new JsonHttpResponseHandler() {
            RetVal retVal;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hideProgress();

                // refresh title
                Log.e("test","callApiGetSpots:"+response.toString());
                MainActivity.g_spots.clear();
                retVal = ServiceManager.inst.parseGetSpots(response, MainActivity.g_spots, true);
                if (retVal.code != ServiceParams.ERR_NONE) {
                    MainActivity.g_curSpot = -1;
                    btnNewJob.setVisibility(View.INVISIBLE);

                    lblTitle.setVisibility(View.VISIBLE);
                    lblTitle.setText("");
                    spnTitle.setVisibility(View.INVISIBLE);
                    // Functions.showToast(getActivity(), retVal.msg);
                } else {
                    MainActivity.g_curSpot = 0;

//                    String[] titles = new String[2];
//                    titles[0] = "현장명 길이 테스트 얼마까지 길어질 것인가 알 수 없습니다.";
//                    titles[0] = titles[0].substring(0,19)+"...";
//                    titles[1] = "현장명 길이 테스트 얼마까지 길어질 것인가 알 수 없습니다.";
//                    titles[1] = titles[1].substring(0,19)+"...";
//                    spnTitle.setAdapter(new MyAdapter(
//                            spnTitle.getContext(), titles));
//                    spnTitle.setVisibility(View.VISIBLE);
//

                    if (MainActivity.g_spots.size() == 1) {
                        lblTitle.setVisibility(View.VISIBLE);
                        String temp = MainActivity.g_spots.get(0).f_name;
                        if(temp.length()>15) {
                            temp = temp.substring(0,15)+"...";
                        }
                        lblTitle.setText(temp);
                        spnTitle.setVisibility(View.INVISIBLE);
                    } else {
                        lblTitle.setVisibility(View.VISIBLE);
                        spnTitle.setVisibility(View.VISIBLE);

                        String[] titles = new String[MainActivity.g_spots.size()];
                        for (int i=0; i<MainActivity.g_spots.size(); i++) {
                            titles[i] = MainActivity.g_spots.get(i).f_name;
                            if(titles[i].length() > 15) {
                                titles[i] = titles[i].substring(0,15)+"...";
                            }
                        }
                        spnTitle.setAdapter(new MyAdapter(
                                spnTitle.getContext(), titles));
                        //Log.e("test","Spinner Size:"+titles.length);
                    }

                    btnNewJob.setVisibility(View.VISIBLE);
                }



                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.updateConfirmWorkFragmentTitle();
                mainActivity.updateConfirmWorkSubFragments();

                updateChildFragments();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                MainActivity.g_curSpot = -1;
                btnNewJob.setVisibility(View.INVISIBLE);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.updateConfirmWorkFragmentTitle();
                mainActivity.updateConfirmWorkSubFragments();

                updateChildFragments();
                hideProgress();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                MainActivity.g_curSpot = -1;
                btnNewJob.setVisibility(View.INVISIBLE);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.updateConfirmWorkFragmentTitle();
                mainActivity.updateConfirmWorkSubFragments();

                updateChildFragments();
                hideProgress();
            }
        };

        showProgress();

        ServiceManager.inst.getSpots(handler);
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    public class SpotSectionsPagerAdapter extends FragmentPagerAdapter {

        public SpotSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

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

            DailyJobsFragment newfrag = new DailyJobsFragment();
            Bundle args = new Bundle();

            //modified by Adonis
            //args.putString(DailyJobsFragment.ARG_DATE, Functions.getDateTimeStringFromToday(position)); //
            args.putString(DailyJobsFragment.ARG_DATE, Functions.getDateTimeStringFromToday(position - 28));
            args.putInt(DailyJobsFragment.ARG_SPOT_ID, spot_id);
            newfrag.setArguments(args);

            registeredFragments.put(position, newfrag);
            Log.e("test", "getItem "+position);
            return newfrag;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            //modified by Adonis
            return 57;
//            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // modified by Adonis
            if(position<57) {
                return Functions.getDateStringWeekday(position - 28);
            }
/*            switch (position) {
                case 0:
                    return Functions.getDateStringWeekday(0);
                case 1:
                    return Functions.getDateStringWeekday(1);
                case 2:
                    return Functions.getDateStringWeekday(2);
                case 3:
                    return Functions.getDateStringWeekday(3);
            }*/
            return null;
        }

        public DailyJobsFragment getFragment(int position) {
            Fragment fragment = registeredFragments.get(position);

            return (DailyJobsFragment)fragment;
        }
    }


}
