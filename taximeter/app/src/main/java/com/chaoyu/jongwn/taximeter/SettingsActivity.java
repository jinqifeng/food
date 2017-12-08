package com.chaoyu.jongwn.taximeter;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
//import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v7.preference.Preference;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_general, new GeneralPreferenceFragment()).commit();
     /*       getSupportFragmentManager().beginTransaction().add(R.id.fragment_tariff, new TariffPreferenceFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_print, new PrintPreferenceFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_account, new AccountPreferenceFragment()).commit();*/

        }

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.pref_general);
        }

        @Override
        public void onDisplayPreferenceDialog(android.support.v7.preference.Preference preference) {

            // Try if the preference is one of our custom Preferences
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }


            if (dialogFragment != null) {
                // The dialog was created (it was one of our custom Preferences), show the dialog for it
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                        ".PreferenceFragment.DIALOG");
            } else {
                // Dialog creation could not be handled here. Try with the super method.
                super.onDisplayPreferenceDialog(preference);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrintPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.pref_print);
        }

        @Override
        public void onDisplayPreferenceDialog(android.support.v7.preference.Preference preference) {

            // Try if the preference is one of our custom Preferences
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }


            if (dialogFragment != null) {
                // The dialog was created (it was one of our custom Preferences), show the dialog for it
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                        ".PreferenceFragment.DIALOG");
            } else {
                // Dialog creation could not be handled here. Try with the super method.
                super.onDisplayPreferenceDialog(preference);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TariffPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.pref_tariff);
        }

        @Override
        public void onDisplayPreferenceDialog(android.support.v7.preference.Preference preference) {

            // Try if the preference is one of our custom Preferences
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }


            if (dialogFragment != null) {
                // The dialog was created (it was one of our custom Preferences), show the dialog for it
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                        ".PreferenceFragment.DIALOG");
            } else {
                // Dialog creation could not be handled here. Try with the super method.
                super.onDisplayPreferenceDialog(preference);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.pref_account);
        }

        @Override
        public void onDisplayPreferenceDialog(android.support.v7.preference.Preference preference) {

            // Try if the preference is one of our custom Preferences
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }


            if (dialogFragment != null) {
                // The dialog was created (it was one of our custom Preferences), show the dialog for it
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                        ".PreferenceFragment.DIALOG");
            } else {
                // Dialog creation could not be handled here. Try with the super method.
                super.onDisplayPreferenceDialog(preference);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
