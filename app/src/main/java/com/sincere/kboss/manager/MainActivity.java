package com.sincere.kboss.manager;

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
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STSelectWorker;
import com.sincere.kboss.stdata.STSpot;

import java.util.ArrayList;

/**
 * Created by Michael on 11/2/2016.
 */
public class MainActivity extends ActivityTempl {
    ViewPager vpContents;
    SectionsPagerAdapter mSectionsPagerAdapter;

    FrameLayout flExtraFragment;

    Fragment extraFragment = null;

    ManageSpotFragment manageSpotFragment = null;
    ConfirmWorkFragment confirmWorkFragment = null;
    FavoriteFragment favoriteFragment = null;

    RelativeLayout rlTab1, rlTab2, rlTab3, rlTab4;
    ImageView imgTab1, imgTab2, imgTab3, imgTab4;
    TextView lblTab1, lblTab2, lblTab3, lblTab4;

    ImageView imgBadge4;

    public static ArrayList<STSpot> g_spots = new ArrayList<>();
    public static int g_curSpot = -1;

    int curTab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

        KbossApplication.getInstance().setSharedPreferencesData("workend",0);

        initUI();

        IntentFilter filter = new IntentFilter(com.sincere.kboss.worker.MainActivity.ACTION_RECEIVE_PUSH);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mHandleMessageDelivery, filter);

    }

    @Override
    public void onBackPressed() {
        if (Functions.gToast!=null && Functions.gToast.getView().isShown()) {
            finish();
            System.exit(0);
        }

        Functions.showToast(MainActivity.this, R.string.back_again);
    }

    void initUI() {

        imgBadge4=(ImageView)findViewById(R.id.imgBadge4);
        KbossApplication app=KbossApplication.getInstance();
        int newnotice=app.getSharedPreferencesData("newnotice",0);
        if ( newnotice==0 )
            imgBadge4.setVisibility(View.VISIBLE);


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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        vpContents = (ViewPager) findViewById(R.id.vpContents);
        vpContents.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("test","Manager - onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("test","Manager - onPageSelected");
                if (curTab == position) {
                    return;
                }

                imgTab1.setImageResource(R.drawable.memo_gray);
                imgTab2.setImageResource(R.drawable.photo_gray);
                imgTab3.setImageResource(R.drawable.contact_gray);
                imgTab4.setImageResource(R.drawable.foursquare_gray);

                lblTab1.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab2.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab3.setTextColor(getResources().getColor(R.color.clr_gray));
                lblTab4.setTextColor(getResources().getColor(R.color.clr_gray));

                curTab = position;

                switch (curTab) {
                    case 0:
                        imgTab1.setImageResource(R.drawable.memo_blue);
                        lblTab1.setTextColor(getResources().getColor(R.color.clr_blue_light));
                        break;

                    case 1:
                        imgTab2.setImageResource(R.drawable.photo_blue);
                        lblTab2.setTextColor(getResources().getColor(R.color.clr_blue_light));
                        break;

                    case 2:
                        imgTab3.setImageResource(R.drawable.contact_blue);
                        lblTab3.setTextColor(getResources().getColor(R.color.clr_blue_light));
                        break;

                    case 3:
                        imgTab4.setImageResource(R.drawable.foursquare_blue);
                        lblTab4.setTextColor(getResources().getColor(R.color.clr_blue_light));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        vpContents.setAdapter(mSectionsPagerAdapter);
        vpContents.setCurrentItem(0);


        flExtraFragment = (FrameLayout) findViewById(R.id.flExtraFragment);

        updateFavoriteFragments();
    }

    public void tabClicked(View v) {
        int clickedTab = (Integer) v.getTag();

        if (curTab == clickedTab) {
            return;
        }
        if(KbossApplication.g_userinfo == null) {
            finish();
            return;
        }
        imgTab1.setImageResource(R.drawable.memo_gray);
        imgTab2.setImageResource(R.drawable.photo_gray);
        imgTab3.setImageResource(R.drawable.contact_gray);
        imgTab4.setImageResource(R.drawable.foursquare_gray);

        lblTab1.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab2.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab3.setTextColor(getResources().getColor(R.color.clr_gray));
        lblTab4.setTextColor(getResources().getColor(R.color.clr_gray));

        curTab = clickedTab;

        switch (curTab) {
            case 0:
                imgTab1.setImageResource(R.drawable.memo_blue);
                lblTab1.setTextColor(getResources().getColor(R.color.clr_blue_light));
                break;

            case 1:
                imgTab2.setImageResource(R.drawable.photo_blue);
                lblTab2.setTextColor(getResources().getColor(R.color.clr_blue_light));
                break;

            case 2:
                imgTab3.setImageResource(R.drawable.contact_blue);
                lblTab3.setTextColor(getResources().getColor(R.color.clr_blue_light));
                break;

            case 3:
                KbossApplication repeatnotice=KbossApplication.getInstance();
                repeatnotice.setSharedPreferencesData("newnotice",1);
                imgBadge4.setVisibility(View.INVISIBLE);
                imgTab4.setImageResource(R.drawable.foursquare_blue);
                lblTab4.setTextColor(getResources().getColor(R.color.clr_blue_light));
                break;
        }

        gotoPrevFragment();

        vpContents.setCurrentItem(clickedTab, true);
    }

    public void updateConfirmWorkFragmentTitle() {
        if(confirmWorkFragment == null) {
            mSectionsPagerAdapter.instantiateItem(vpContents, 1);
        }
        if (confirmWorkFragment != null) {
            confirmWorkFragment.updateTitle();
        }
    }

    public void updateConfirmWorkSubFragments() {
        if(confirmWorkFragment == null) {
            mSectionsPagerAdapter.instantiateItem(vpContents, 1);
        }
        if (confirmWorkFragment != null) {
            confirmWorkFragment.updateChildFragments();
        }
    }

    public void updateManageFragments() {
        if(manageSpotFragment == null) {
            mSectionsPagerAdapter.instantiateItem(vpContents, 0);
        }
        if (manageSpotFragment != null) {
            manageSpotFragment.updateChildFragments();
        }
    }


    public void updateFavoriteFragments()
    {
        if(favoriteFragment == null) {
            mSectionsPagerAdapter.instantiateItem(vpContents, 2);
        }
        if(favoriteFragment != null) {
            Log.e("test","MainActivity updateFavoriteFragments");
            favoriteFragment.updateFragment();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment newFragment = null;

            switch (position) {
                case 0:
                    newFragment = new ManageSpotFragment();
                    manageSpotFragment = (ManageSpotFragment) newFragment;
                    break;

                case 1:
                    newFragment = new ConfirmWorkFragment();
                    confirmWorkFragment = (ConfirmWorkFragment) newFragment;
                    break;

                case 2:
                    newFragment = new FavoriteFragment();
                    favoriteFragment = (FavoriteFragment) newFragment;
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

    public void gotoRegisterRequestFragment() {
        extraFragment = new RegisterRequestFragment();
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

    public void gotoLinkSpotFragment() {
        extraFragment = new LinkSpotFragment();
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

    public void gotoSpotInfoFragment(STSpot spot) {
        extraFragment = new SpotInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(SpotInfoFragment.ARG_SPOT, spot);
        extraFragment.setArguments(args);

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

    public void gotoModifyWorkInfoFragment(STSelectWorker worker) {
        extraFragment = new ModifyWorkInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(AddFavoriteFragment.ARG_WORKER, worker);
        extraFragment.setArguments(args);

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

    public void gotoAddFavoriteFragment(STSelectWorker worker) {
        extraFragment = new AddFavoriteFragment();
        Bundle args = new Bundle();
        args.putParcelable(AddFavoriteFragment.ARG_WORKER, worker);
        extraFragment.setArguments(args);

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
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            //transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
                            transaction.remove(extraFragment);
                            transaction.commit();

                            extraFragment = null;
                        }
                    }
                });
    }

    private final BroadcastReceiver mHandleMessageDelivery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateManageFragments();
        }
    };
}
