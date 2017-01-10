package com.sincere.kboss.manager;

import android.support.v4.app.Fragment;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by Michael on 11/2/2016.
 */
public abstract class FragmentTempl extends Fragment {
    protected JsonHttpResponseHandler handler;

    public void setupProgress(String text) {
        //MainActivity mainActivity = (MainActivity) getActivity();

        //mainActivity.setupProgress(text);
    }

    public void setupProgress(int resid) {
        //MainActivity mainActivity = (MainActivity) getActivity();

        //mainActivity.setupProgress(resid);
    }

    public void showProgress() {
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.showProgress();
    }

    public void hideProgress() {
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.hideProgress();
    }
}
