package com.chaoyu.jongwn.taximeter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePreference extends DialogPreference {
	private int lastHour = 0;
	private int lastMinut = 0;
	private int lastSecond = 0;
	private String timeval;
	private CharSequence mSummary;
	private TimePicker picker = null;
	public static int getHour(String dateval) {
		String[] pieces = dateval.split(":");
		return (Integer.parseInt(pieces[0]));
	}

	public static int getMinut(String dateval) {
		String[] pieces = dateval.split(":");
		return (Integer.parseInt(pieces[1]));
	}

	public static int getSecond(String dateval) {
		String[] pieces = dateval.split(":");
		return (Integer.parseInt(pieces[2]));
	}

	public TimePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	@Override
	protected View onCreateDialogView() {
		picker = new TimePicker(getContext());

		// setCalendarViewShown(false) attribute is only available from API level 11
	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			picker.setCalendarViewShown(false);
		}
*/
		picker.setHour(lastHour);
		picker.setMinute(lastMinut);
		return (picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

	//	picker.updateDate(lastYear, lastMonth , lastDate);
		picker.setHour(lastHour);
		picker.setMinute(lastMinut);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			lastHour = picker.getHour();
			lastMinut = picker.getMinute();
		//	lastSecond = picker.getDayOfMonth();
		//	Integer record = lastMonth+1;
			String datevale = "";
		/*	datevale = String.valueOf(lastYear) + "-"
					+ String.valueOf(lastMonth) + "-"
					+ String.valueOf(lastDate);
*/
			datevale = datevale+lastHour + ":"
					+ lastMinut;

			if (callChangeListener(datevale)) {
				persistString(datevale);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		timeval = null;

		if (restoreValue) {
			if (defaultValue == null) {
				Calendar cal = Calendar.getInstance();

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
				//dateval = getPersistedString(formatted);

				//timeval = formatted;
				lastHour = hour;
				lastMinut = minute;
		//		lastSecond = getSecond(timeval);
			} else {
				timeval = getPersistedString(defaultValue.toString());
				lastHour = getHour(timeval);
				lastMinut = getMinut(timeval);
		//		lastSecond = getSecond(timeval);
			}
		} else {
			timeval = defaultValue.toString();
			lastHour = getHour(timeval);
			lastMinut = getMinut(timeval);
		//	lastSecond = getSecond(timeval);
		}

	}

	public void setText(String text) {
		final boolean wasBlocking = shouldDisableDependents();

		timeval = text;

		persistString(text);

		final boolean isBlocking = shouldDisableDependents();
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	public String getText() {
		return timeval;
	}

	public CharSequence getSummary() {
		return mSummary;
	}

	public void setSummary(CharSequence summary) {
		if (summary == null && mSummary != null || summary != null
				&& !summary.equals(mSummary)) {
			mSummary = summary;
			notifyChanged();
		}
	}
	private static class SavedState extends BaseSavedState {
		// Member that holds the setting's value
		// Change this data type to match the type saved by your Preference
		String value;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			// Get the current preference's value
			value = source.readString();  // Change this to read the appropriate data type
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			// Write the preference's value
			dest.writeString(value);  // Change this to write the appropriate data type
		}

		// Standard creator object using an instance of this class
		public static final Creator<SavedState> CREATOR =
				new Creator<TimePreference.SavedState>() {

					public TimePreference.SavedState createFromParcel(Parcel in) {
						return new TimePreference.SavedState(in);
					}

					public TimePreference.SavedState[] newArray(int size) {
						return new TimePreference.SavedState[size];
					}
				};
	}
	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		// Check whether this Preference is persistent (continually saved)
		if (isPersistent()) {
			// No need to save instance state since it's persistent,
			// use superclass state
			return superState;
		}

		// Create instance of custom BaseSavedState
		final SavedState myState = new SavedState(superState);
		// Set the state's value with the class member that holds current
		// setting value
		myState.value = timeval;
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// Check whether we saved the state in onSaveInstanceState
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save the state, so call superclass
			super.onRestoreInstanceState(state);
			return;
		}

		// Cast state to custom BaseSavedState and pass to superclass
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());

		// Set this Preference's widget to reflect the restored state
		timeval = myState.value;
		lastHour = getHour(timeval);
		lastMinut = getMinut(timeval);
	//	lastSecond = getSecond(timeval);
	//	picker.updateDate(lastHour, lastMinut, lastSecond);
	}
}
