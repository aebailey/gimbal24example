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
package tesco;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.gimbal.logging.GimbalLogConfig;
import com.gimbal.logging.GimbalLogLevel;

import baileyae.gimbal24example.GimbalDAO;
import baileyae.gimbal24example.R;

// NOTE: Using deprecated apis to support api levels down to 8 and keep
// code relatively straight forward
@SuppressWarnings("deprecation")
public class SettingsProd extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prod_pref);
        PreferenceManager.setDefaultValues(this,R.xml.prod_pref,false);
        MultiSelectListPreference prodPreference = (MultiSelectListPreference)findPreference("pList");
        //prodPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        //    @Override
        //    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //        if (newValue == Boolean.TRUE) {

 //               }
  //              else {
//
  //              }
    //            return true;
      //      }
       // });


    }
}
