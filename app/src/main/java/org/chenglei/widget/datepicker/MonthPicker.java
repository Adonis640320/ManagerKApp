package org.chenglei.widget.datepicker;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.sincere.kboss.R;

public class MonthPicker extends LinearLayout implements NumberPicker.OnValueChangeListener {
	
	private NumberPicker mYearPicker;
	private NumberPicker mMonthPicker;
	
	private Calendar mCalendar;
	
	private OnMonthChangedListener mOnMonthChangedListener;
	
	private LayoutInflater mLayoutInflater;

	public MonthPicker(Context context) {
		this(context, null);
	}
	
	public MonthPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		init();
	}
	
	private void init() {
		mLayoutInflater.inflate(R.layout.month_picker_layout, this, true);
		mYearPicker = (NumberPicker) findViewById(R.id.year_picker);
		mMonthPicker = (NumberPicker) findViewById(R.id.month_picker);
		
		mYearPicker.setOnValueChangeListener(this);
		mMonthPicker.setOnValueChangeListener(this);
		
		if (!getResources().getConfiguration().locale.getCountry().equals("CN")
				&& !getResources().getConfiguration().locale.getCountry().equals("TW")) {
			
			String[] monthNames = getResources().getStringArray(R.array.month_name);
			mMonthPicker.setCustomTextArray(monthNames);
			
		}
		
		mCalendar = Calendar.getInstance();
		setDate(mCalendar.getTime());
	}
	
	public MonthPicker setDate(Date date) {
		mCalendar.setTime(date);
		
		mYearPicker.setCurrentNumber(mCalendar.get(Calendar.YEAR));
		mMonthPicker.setCurrentNumber(mCalendar.get(Calendar.MONTH) + 1);
		
		return this;
	}

	@Override
	public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
		
		if (picker == mYearPicker) {
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
			mCalendar.set(newVal, mCalendar.get(Calendar.MONTH), 1);
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		} else if (picker == mMonthPicker) {
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.set(mCalendar.get(Calendar.YEAR), newVal - 1, 1);
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}
		
		notifyDateChanged();
	}
	
	/**
     * The callback used to indicate the user changes\d the date.
     */
    public interface OnMonthChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *            with {@link java.util.Calendar}.
         */
        void onMonthChanged(MonthPicker view, int year, int monthOfYear);
    }
    
    public MonthPicker setOnMonthChangedListener(OnMonthChangedListener l) {
    	mOnMonthChangedListener = l;
    	return this;
    }
    
    private void notifyDateChanged() {
    	if (mOnMonthChangedListener != null) {
    		mOnMonthChangedListener.onMonthChanged(this, getYear(), getMonth());
    	}
    }
    
    public int getYear() {
    	return mCalendar.get(Calendar.YEAR);
    }
    
    public int getMonth() {
    	return mCalendar.get(Calendar.MONTH) + 1;
    }
    
    public MonthPicker setRowNumber(int rowNumber) {
    	mYearPicker.setRowNumber(rowNumber);
    	mMonthPicker.setRowNumber(rowNumber);
    	return this;
    }
    
    public MonthPicker setTextSize(float textSize) {
    	mYearPicker.setTextSize(textSize);
    	mMonthPicker.setTextSize(textSize);
    	return this;
    }
    
    public MonthPicker setFlagTextSize(float textSize) {
    	mYearPicker.setFlagTextSize(textSize);
    	mMonthPicker.setFlagTextSize(textSize);
    	return this;
    }
    
    public MonthPicker setTextColor(int color) {
    	mYearPicker.setTextColor(color);
    	mMonthPicker.setTextColor(color);
    	return this;
    }
    
    public MonthPicker setFlagTextColor(int color) {
    	mYearPicker.setFlagTextColor(color);
    	mMonthPicker.setFlagTextColor(color);
    	return this;
    }
    
    public MonthPicker setBackground(int color) {
    	super.setBackgroundColor(color);
    	mYearPicker.setBackground(color);
    	mMonthPicker.setBackground(color);
    	return this;
    }

}
