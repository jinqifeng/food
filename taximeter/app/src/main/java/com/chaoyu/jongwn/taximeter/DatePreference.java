package com.chaoyu.jongwn.taximeter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePreference extends DialogPreference {
	private int lastDate = 0;
	private int lastMonth = 0;
	private int lastYear = 0;
	private String dateval;
	private CharSequence mSummary;
	private DatePicker picker = null;
	public static int getYear(String dateval) {
		String[] pieces = dateval.split("-");
		return (Integer.parseInt(pieces[0]));
	}

	public static int getMonth(String dateval) {
		String[] pieces = dateval.split("-");
		return (Integer.parseInt(pieces[1]));
	}

	public static int getDate(String dateval) {
		String[] pieces = dateval.split("-");
		return (Integer.parseInt(pieces[2]));
	}

	public DatePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	@Override
	protected View onCreateDialogView() {
		picker = new DatePicker(getContext());

		// setCalendarViewShown(false) attribute is only available from API level 11
	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			picker.setCalendarViewShown(false);
		}
*/
		picker.updateDate(lastYear, lastMonth, lastDate);
		return (picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		picker.updateDate(lastYear, lastMonth , lastDate);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			lastYear = picker.getYear();
			lastMonth = picker.getMonth();
			lastDate = picker.getDayOfMonth();
			Integer record = lastMonth+1;
			String datevale = "";
		/*	datevale = String.valueOf(lastYear) + "-"
					+ String.valueOf(lastMonth) + "-"
					+ String.valueOf(lastDate);
*/
			datevale = datevale+lastYear + "-"
					+ record + "-"
					+ lastDate;

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
		dateval = null;

		if (restoreValue) {
			if (defaultValue == null) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				Date ln = cal.getTime();
				String formatted = format1.format(ln);
				//dateval = getPersistedString(formatted);

				dateval = formatted;
				lastYear = getYear(dateval);
				lastMonth = getMonth(dateval)-1;
				lastDate = getDate(dateval);
			} else {
				dateval = getPersistedString(defaultValue.toString());
				lastYear = getYear(dateval);
				lastMonth = getMonth(dateval);
				lastDate = getDate(dateval);
			}
		} else {
			dateval = defaultValue.toString();
			lastYear = getYear(dateval);
			lastMonth = getMonth(dateval);
			lastDate = getDate(dateval);
		}

	}

	public void setText(String text) {
		final boolean wasBlocking = shouldDisableDependents();

		dateval = text;

		persistString(text);

		final boolean isBlocking = shouldDisableDependents();
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	public String getText() {
		return dateval;
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
		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>() {

					public SavedState createFromParcel(Parcel in) {
						return new SavedState(in);
					}

					public SavedState[] newArray(int size) {
						return new SavedState[size];
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
		myState.value = dateval;
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
		dateval = myState.value;
		lastYear = getYear(dateval);
		lastMonth = getMonth(dateval);
		lastDate = getDate(dateval);
		picker.updateDate(lastYear, lastMonth, lastDate);
	}
}
