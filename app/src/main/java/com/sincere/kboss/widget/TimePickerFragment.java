package com.sincere.kboss.widget;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.app.DialogFragment;
import java.util.Calendar;
import java.util.Vector;

import android.app.TimePickerDialog.OnTimeSetListener;

import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;

/**
 * Created by SunMS on 1/7/2017.
 */

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
    public interface TimePickerDialogHandler{
        public void onSetTime(int hour,int m);
    }
    TimePickerDialogHandler m_timepickerdialoghandler;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker

        int hour = 7;
        int minute = 0;

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time
        m_timepickerdialoghandler.onSetTime(hourOfDay,minute);
        //Get reference of host activity (XML Layout File) TextView widget
    }
    public void setTimePickerDialogHandlers(TimePickerDialogHandler handlers) {
        m_timepickerdialoghandler = handlers;
    }
}
