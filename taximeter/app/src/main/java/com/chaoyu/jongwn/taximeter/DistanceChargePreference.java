package com.chaoyu.jongwn.taximeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DistanceChargePreference extends DialogPreference {
	private String to1 = "0";
	private String ch1 = "0";
	private String ch2 = "0";
	EditText etTo1 ;
	EditText tvChgP1;
	TextView tvFrom2;
	EditText tvChgP2;
	private String dateval;
	private CharSequence mSummary;


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

	public DistanceChargePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
		//setPersistent(false);
		setDialogLayoutResource(R.layout.distance_charge);
	}
/*
	@Override
	protected View onCreateDialogView() {
		picker = new DatePicker(getContext());

		// setCalendarViewShown(false) attribute is only available from API level 11
	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			picker.setCalendarViewShown(false);
		}
*/
/*		picker.updateDate(lastYear, lastMonth, lastDate);
		return (picker);
	}
*/
	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		etTo1 = (EditText)v.findViewById(R.id.tvTo1) ;
		tvChgP1 = (EditText)v.findViewById(R.id.tvChgP1) ;
		tvFrom2 = (TextView)v.findViewById(R.id.tvFrom2) ;
		tvChgP2 = (EditText)v.findViewById(R.id.tvChgP2) ;

		etTo1.setText(String.valueOf(to1));
		tvChgP1.setText(String.valueOf(ch1));
		tvChgP2.setText(String.valueOf(ch2));
		tvFrom2.setText(String.valueOf(to1));

		etTo1.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
				tvFrom2.setText(s);
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			to1 = etTo1.getText().toString();
			ch1 = tvChgP1.getText().toString();
			ch2 = tvChgP2.getText().toString();
			String datevale = "";
		/*	datevale = String.valueOf(lastYear) + "-"
					+ String.valueOf(lastMonth) + "-"
					+ String.valueOf(lastDate);
*/
			datevale = datevale+to1 + "-"
					+ ch1 + "-"
					+ ch2;

			if (callChangeListener(datevale)) {
				persistString(datevale);
			}
			SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
			mPref.edit().putString("distance_charge",datevale);
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

				  to1 = "0";
				  ch1 = "0";
				  ch2 = "0";
			} else {
				dateval = getPersistedString(defaultValue.toString());
				String[] split = dateval.split("-");
				to1 = split[0];
				ch1 = split[1];
				ch2 = split[2];
			}
		} else {
			dateval = defaultValue.toString();
			String[] split = dateval.split("-");
			to1 = split[0];
			ch1 = split[1];
			ch2 = split[2];
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
		public static final Creator<SavedState> CREATOR =
				new Creator<DistanceChargePreference.SavedState>() {

					public DistanceChargePreference.SavedState createFromParcel(Parcel in) {
						return new DistanceChargePreference.SavedState(in);
					}

					public DistanceChargePreference.SavedState[] newArray(int size) {
						return new DistanceChargePreference.SavedState[size];
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
		String[] split = dateval.split("-");
		to1 = split[0];
		ch1 = split[1];
		ch2 = split[2];
		etTo1.setText(String.valueOf(to1));
		tvChgP1.setText(String.valueOf(ch1));
		tvChgP2.setText(String.valueOf(ch2));
		tvFrom2.setText(String.valueOf(to1));
	}
}
