package com.derric.quickbar.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.derric.quickbar.ChooseAppsActivity;
import com.derric.quickbar.OrderAppsActivity;
import com.derric.quickbar.QuickBarUtils;
import com.derric.quickbar.R;
import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsMenu extends PreferenceFragmentCompat {

    private ArrayList<AppInfo> appInfos;
    private boolean isSettingsChanged;

    public SettingsMenu(ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    public boolean isSettingsChanged() {
        return this.isSettingsChanged;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey);
        Toast.makeText(getContext(),"Scroll down for more settings....", Toast.LENGTH_SHORT).show();
//        getPreferenceScreen().getExtras().putSerializable(AppConstants.APP_INFOS, appInfos);
        Preference chooseApp = findPreference("chooseApps");
        chooseApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent chooseAppsIntent = new Intent(getActivity(), ChooseAppsActivity.class);
                chooseAppsIntent.putExtra(AppConstants.APP_INFOS, appInfos);
                startActivity(chooseAppsIntent);
                return true;
            }
        });

        Preference orderApps = findPreference("orderApps");
        orderApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent orderAppsIntent = new Intent(getActivity(), OrderAppsActivity.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                //Show only user selected Apps here
                ArrayList<AppInfo> userSelectedApps = QuickBarUtils.getUserSelectedApps(preferences.getStringSet("selectedApps", null), appInfos);
                if(userSelectedApps == null || userSelectedApps.isEmpty()){
                    userSelectedApps = chooseRandomAppsAndSave();
                }
                orderAppsIntent.putExtra(AppConstants.APP_INFOS, userSelectedApps);
                startActivity(orderAppsIntent);
                return true;
            }
        });

//        SeekBarPreference barTransparencySeekBar = (SeekBarPreference) findPreference("quickbarTransparency");
//        barTransparencySeekBar

//        hideQuickBarSeconds
        SeekBarPreference hideQuickBarSlider = (SeekBarPreference) findPreference("hideQuickBarSeconds");

    }

    private ArrayList<AppInfo> chooseRandomAppsAndSave(){
        //There are no user selected apps, means user is using the app for first time
        //show some random 5 apps
        ArrayList<AppInfo> appsToShow = new ArrayList<>();
        PackageManager packageManager = getContext().getPackageManager();
        int count = 0;
        for (AppInfo app : appInfos) {
            if (count <= 5) {
                Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(app.getPackageName());
                if (mainActivityIntent != null) {
                    appsToShow.add(app);
//                    app.setSelected(true);
//                    app.setPosition(count);
                    count++;
                }
            }
        }
        //save these 5 apps
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> selectedApps = new HashSet<>();
        int position = 0;
        for (AppInfo appInfo : appsToShow) {
            selectedApps.add(appInfo.getPackageName() + ":" + position++);
        }
        editor.putStringSet("selectedApps", selectedApps);
        editor.commit();
        return appsToShow;
    }


}
