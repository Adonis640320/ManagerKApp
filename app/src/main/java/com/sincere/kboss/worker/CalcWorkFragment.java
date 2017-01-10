package com.sincere.kboss.worker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sincere.kboss.R;

/**
 * Created by Michael on 11/2/2016.
 */
public class CalcWorkFragment extends FragmentTempl {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_notready, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity mainActivity = (MainActivity) getActivity();
        //mainActivity.updateTitleBar(R.string.tab_control_calendar, false);
    }
}
