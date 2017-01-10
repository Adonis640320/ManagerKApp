package com.sincere.kboss.worker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.R;
import com.sincere.kboss.UserInfoActivity;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STJobWorker;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class MainActivity extends ActivityTempl {

    RelativeLayout rlTab1, rlTab2, rlTab3, rlTab4;
    ImageView imgTab1, imgTab2, imgTab3, imgTab4;
    TextView lblTab1, lblTab2, lblTab3, lblTab4;
    ImageView imgBadge;
    ImageView imgBadge4;
    ViewPager vpContents;
    SectionsPagerAdapter mSectionsPagerAdapter;

    FrameLayout flExtraFragment;

    android.support.v4.app.Fragment extraFragment = null;

    int curTab = -1;

    public static final String ACTION_RECEIVE_PUSH = "ACTION.receiver_push";

    public static final String EXTRA_GOTO_USERINFO = "goto_userinfo";
    boolean goto_userinfo = false;

    public static final String EXTRA_GOTO_SETTINGS = "goto_settings";
    boolean goto_settings = false;

    public static final String EXTRA_SPOT_FRAGMENT = "goto_spot_fragment";
    boolean goto_spot_fragment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);

        initUI();

        goto_settings = getIntent().getBooleanExtra(EXTRA_GOTO_SETTINGS, false);

        if (goto_settings) {
            tabClicked(rlTab4);
        } else {
            if(KbossApplication.getInstance().getSharedPreferencesData("workend",0) == 1) {
                tabClicked(rlTab2);
            } else tabClicked(rlTab1);
            KbossApplication.getInstance().setSharedPreferencesData("workend",0);
        }

        goto_userinfo = getIntent().getBooleanExtra(EXTRA_GOTO_USERINFO, false);

        if (goto_userinfo) {
            gotoUserInfoActivity(null);
        }

        goto_spot_fragment = getIntent().getBooleanExtra(EXTRA_SPOT_FRAGMENT, false);

        if (goto_spot_fragment) {
            gotoSpotFragment();
        }

        IntentFilter filter = new IntentFilter(MainActivity.ACTION_RECEIVE_PUSH);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mHandleMessageDelivery, filter);
    }

    @Override
    public void onBackPressed() {
        if (Functions.gToast != null && Functions.gToast.getView().isShown()) {
            finish();
            System.exit(0);
        }

        Functions.showToast(MainActivity.this, R.string.back_again);
    }

    public void showTab1Badge(boolean show) {
        imgBadge.setVisibility(show?View.VISIBLE:View.INVISIBLE);
    }

    void initUI() {
        imgTab1 = (ImageView) findViewById(R.id.imgTab1);
        imgTab2 = (ImageView) findViewById(R.id.imgTab2);
        imgTab3 = (ImageView) findViewById(R.id.imgTab3);
        imgTab4 = (ImageView) findViewById(R.id.imgTab4);

        lblTab1 = (TextView) findViewById(R.id.lblTab1);
        lblTab2 = (TextView) findViewById(R.id.lblTab2);
        lblTab3 = (TextView) findViewById(R.id.lblTab3);
        lblTab4 = (TextView) findViewById(R.id.lblTab4);

        rlTab1 = (RelativeLayout) findViewById(R.id.rlTab1);
        rlTab2 = (RelativeLayout) findViewById(R.id.rlTab2);
        rlTab3 = (RelativeLayout) findViewById(R.id.rlTab3);
        rlTab4 = (RelativeLayout) findViewById(R.id.rlTab4);

        rlTab1.setTag(0);
        rlTab2.setTag(1);
        rlTab3.setTag(2);
        rlTab4.setTag(3);

        imgBadge = (ImageView) findViewById(R.id.imgBadge);
        imgBadge4=(ImageView)findViewById(R.id.imgBadge4);
        KbossApplication app=KbossApplication.getInstance();
        int newnotice=app.getSharedPreferencesData("newnotice",0);
        if ( newnotice==0 )
            imgBadge4.setVisibility(View.VISIBLE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        vpContents = (ViewPager) findViewById(R.id.vpContents);
        vpContents.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("test","MainActivity - PageScrolled:"+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("test","MainActivity - PageSelected:"+position);
                rlTab1.setBackgroundColor(getResources().getColor(R.color.white));
                rlTab2.setBackgroundColor(getResources().getColor(R.color.white));
                rlTab3.setBackgroundColor(getResources().getColor(R.color.white));
                rlTab4.setBackgroundColor(getResources().getColor(R.color.white));

                imgTab1.setImageResource(R.drawable.alarmoff);
                imgTab2.setImageResource(R.drawable.clockoff);
                imgTab3.setImageResource(R.drawable.calendaroff);
                imgTab4.setImageResource(R.drawable.paperoff);

                lblTab1.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab2.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab3.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab4.setTextColor(getResources().getColor(R.color.clr_gray));

                curTab = position;

                switch (curTab) {
                    case 0:
                        if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                            Functions.showToastLong(MainActivity.this, R.string.update_userinfo);
                        }

                        imgTab1.setImageResource(R.drawable.alarmon);
                        //lblTab1.setTextColor(getResources().getColor(R.color.white));
                        lblTab1.setTextColor(getResources().getColor(R.color.clr_red_dark));
                        //rlTab1.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                        rlTab1.setBackgroundColor(getResources().getColor(R.color.white));
                        break;

                    case 1:
                        if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                            Functions.showToastLong(MainActivity.this, R.string.update_userinfo);
                        }

                        imgTab2.setImageResource(R.drawable.clockon);
                        //lblTab2.setTextColor(getResources().getColor(R.color.white));
                        lblTab2.setTextColor(getResources().getColor(R.color.clr_red_dark));
                        //rlTab2.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                        rlTab2.setBackgroundColor(getResources().getColor(R.color.white));
                        break;

                    case 2:
                        imgTab3.setImageResource(R.drawable.calendaron);
                        //lblTab3.setTextColor(getResources().getColor(R.color.white));
                        lblTab3.setTextColor(getResources().getColor(R.color.clr_red_dark));
                        //rlTab3.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                        rlTab3.setBackgroundColor(getResources().getColor(R.color.white));
                        break;

                    case 3:
                        imgTab4.setImageResource(R.drawable.paperon);
                        //lblTab4.setTextColor(getResources().getColor(R.color.white));
                        lblTab4.setTextColor(getResources().getColor(R.color.clr_red_dark));
                        //rlTab4.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                        rlTab4.setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        flExtraFragment = (FrameLayout) findViewById(R.id.flExtraFragment);
        vpContents.setAdapter(mSectionsPagerAdapter);

        vpContents.setCurrentItem(0);

    }

    public void tabClicked(View v) {

        int clickedTab = (Integer) v.getTag();
        if(clickedTab == 0) {
            showTab1Badge(false);
        }
        if (curTab == clickedTab) {
            return;
        }
        rlTab1.setBackgroundColor(getResources().getColor(R.color.white));
        rlTab2.setBackgroundColor(getResources().getColor(R.color.white));
        rlTab3.setBackgroundColor(getResources().getColor(R.color.white));
        rlTab4.setBackgroundColor(getResources().getColor(R.color.white));

        imgTab1.setImageResource(R.drawable.alarmoff);
        imgTab2.setImageResource(R.drawable.clockoff);
        imgTab3.setImageResource(R.drawable.calendaroff);
        imgTab4.setImageResource(R.drawable.paperoff);

        lblTab1.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab2.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab3.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab4.setTextColor(getResources().getColor(R.color.clr_gray));

        curTab = clickedTab;
        if(KbossApplication.g_userinfo == null) {
            finish();
            return;
        }
        switch (curTab) {
            case 0:
                if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                    Functions.showToastLong(this, R.string.update_userinfo);
                }

                imgTab1.setImageResource(R.drawable.alarmon);
                //lblTab1.setTextColor(getResources().getColor(R.color.white));
                lblTab1.setTextColor(getResources().getColor(R.color.clr_red_dark));
                //rlTab1.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                rlTab1.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case 1:
                if (KbossApplication.g_userinfo.minimumRequirement() == false) {
                    Functions.showToastLong(this, R.string.update_userinfo);
                }

                imgTab2.setImageResource(R.drawable.clockon);
                //lblTab2.setTextColor(getResources().getColor(R.color.white));
                lblTab2.setTextColor(getResources().getColor(R.color.clr_red_dark));
                //rlTab2.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                rlTab2.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case 2:
                imgTab3.setImageResource(R.drawable.calendaron);
                //lblTab3.setTextColor(getResources().getColor(R.color.white));
                lblTab3.setTextColor(getResources().getColor(R.color.clr_red_dark));
                //rlTab3.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                rlTab3.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case 3:
                KbossApplication repeatnotice=KbossApplication.getInstance();
                repeatnotice.setSharedPreferencesData("newnotice",1);
                imgBadge4.setVisibility(View.INVISIBLE);
                imgTab4.setImageResource(R.drawable.paperon);
                //lblTab4.setTextColor(getResources().getColor(R.color.white));
                lblTab4.setTextColor(getResources().getColor(R.color.clr_red_dark));
                //rlTab4.setBackgroundColor(getResources().getColor(R.color.clr_red_dark));
                rlTab4.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }

        gotoPrevFragment();

        vpContents.setCurrentItem(clickedTab, true);
    }

    public void gotoPointsFragment() {

        extraFragment = new PointFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flExtraFragment, extraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        flExtraFragment.setAlpha(0f);
        flExtraFragment.setVisibility(View.VISIBLE);
        flExtraFragment.animate().alpha(1f).setDuration(250)
                .setListener(null);

    }

    public void gotoSpotFragment() {

        extraFragment = new GotoSpotFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flExtraFragment, extraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        flExtraFragment.setAlpha(0f);
        flExtraFragment.setVisibility(View.VISIBLE);
        flExtraFragment.animate().alpha(1f).setDuration(250)
                .setListener(null);

    }

    public void gotoJobDetailFragment(JobListFragment frag,STJobWorker job) {

        extraFragment = new JobDetailFragment();
        extraFragment.setTargetFragment(frag, 1);

        Bundle bundle = new Bundle();
        bundle.putParcelable(JobDetailFragment.ARG_JOB, job);
        extraFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flExtraFragment, extraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        flExtraFragment.setAlpha(0f);
        flExtraFragment.setVisibility(View.VISIBLE);
        flExtraFragment.animate().alpha(1f).setDuration(250)
                .setListener(null);

    }

    public void gotoWorkHistoryFragment(STJobWorker workHistory) {

        extraFragment = new WorkHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WorkHistoryFragment.ARG_HISTORY, workHistory);
        extraFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flExtraFragment, extraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        flExtraFragment.setAlpha(0f);
        flExtraFragment.setVisibility(View.VISIBLE);
        flExtraFragment.animate().alpha(1f).setDuration(250)
                .setListener(null);

    }

    public void gotoMapViewFragment(STJobWorker job) {

        extraFragment = new MapViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MapViewFragment.ARG_JOB, job);
        extraFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flExtraFragment, extraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        flExtraFragment.setAlpha(0f);
        flExtraFragment.setVisibility(View.VISIBLE);
        flExtraFragment.animate().alpha(1f).setDuration(250)
                .setListener(null);


    }

    public void gotoPrevFragment() {
        flExtraFragment.animate().alpha(0f).setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flExtraFragment.setVisibility(View.INVISIBLE);

                        if (extraFragment != null) {
                            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            //transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
                            transaction.remove(extraFragment);
                            transaction.commit();

                            extraFragment = null;
                        }
                    }
                });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment newFragment = null;

            switch (position) {
                case 0:
                    // Create new fragment and transaction
                    showTab1Badge(false);
                    newFragment = new JobListFragment();
                    break;

                case 1:

                    newFragment = new WorkListFragment();
                    break;

                case 2:
                    newFragment = new CalcWorkFragment();
                    break;

                case 3:
                    newFragment = new SettingsFragment();
                    break;
            }

            return newFragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    public void gotoUserInfoActivity(View v) {
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private final BroadcastReceiver mHandleMessageDelivery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "N Push");
            if(intent.hasExtra("addjob")) {
                //int addjob = intent.getIntExtra("addjob");
                //if (addjob == 1) {
                    showTab1Badge(true);
                //}
            }
            if(intent.hasExtra("workend")) {
                vpContents.setCurrentItem(1);
            }
        }
    };

//    public class PushReceiver extends BroadcastReceiver {
//
//        public static final String ACTION_RECEIVE_PUSH = "ACTION.receiver_push";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String val=intent.getStringExtra("push");
//            if ( Integer.valueOf(val)==1 )
//            {
//                showTab1Badge(true);
//            }
//        }
//    }
}
