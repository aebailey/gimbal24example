/**
 * Copyright (C) 2014 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package baileyae.gimbal24example;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;

// NOTE: Using deprecated apis to support api levels down to 8 and keep
// code relatively straight forward
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference placeMonitoringPreferend = findPreference(GimbalDAO.PLACE_MONITORING_PREFERENCE);
        placeMonitoringPreferend.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue == Boolean.TRUE) {
                    PlaceManager.getInstance().startMonitoring();
                }
                else {
                    PlaceManager.getInstance().stopMonitoring();
                }
                return true;
            }
        });

        Preference resetAppInstanceIdPreference = findPreference("pref_reset_app_instance_id");
        resetAppInstanceIdPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Gimbal.resetApplicationInstanceIdentifier();
                Toast.makeText(SettingsActivity.this, "App Instance ID reset", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

}
