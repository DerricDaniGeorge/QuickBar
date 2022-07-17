package com.derric.quickbar.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.derric.quickbar.QuickBarUtils;
import com.derric.quickbar.R;
import com.derric.quickbar.adapters.ChooseAppsAdapter;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAppsFragment extends Fragment {
    //    private static final String APP_INFOS = "app_infos";
    private ArrayList<AppInfo> appInfos;

    //A public no arugment constructor is must for all subclasses of Fragment
    public ChooseAppsFragment() {
    }

    private ChooseAppsAdapter adapter;
//    private boolean isAllAppsSelected;

    public ChooseAppsFragment(ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;

        QuickBarUtils.sortAppsByName(appInfos);
    }

//    public boolean getAllAppsSelected() {
//        return this.isAllAppsSelected;
//    }

    public ChooseAppsAdapter getAdapter() {
        return this.adapter;
    }


    // TODO: Rename and change types and number of parameters
//    public static ChooseAppsFragment newInstance(List<AppInfo> appInfos) {
//        ChooseAppsFragment fragment = new ChooseAppsFragment();
//        Bundle bundle = new Bundle();
//        ArrayList<AppInfo> apps = new ArrayList<>();
//        apps.addAll(appInfos);
//        bundle.putSerializable(APP_INFOS,apps);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Detect actionbar of the activity where this fragment is present
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_choose_apps, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
//        PackageManager packageManager = getContext().getPackageManager();
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager,getContext());
//        List<AppInfo> appsWithActivity = new ArrayList<>();
//        for (AppInfo appInfo : appInfos) {
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            if (mainActivityIntent != null) {
//                appsWithActivity.add(appInfo);
//            }
//        }
        //If user selected apps are there, then when showing the list, mark those selected apps checked
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> selectedApps = preferences.getStringSet("selectedApps", null);
        List<String> appNames = new ArrayList<>();
        if (selectedApps != null) {
            for (String app : selectedApps) {
                String[] split = app.split(":");
                appNames.add(split[0]);
            }
        }
        if (selectedApps != null) {
            for (AppInfo appInfo : appInfos) {
                String[] split = appInfo.getPackageName().split(":");
                if (appNames.contains(split[0])) {
                    appInfo.setSelected(true);
                }
            }
        }
        adapter = new ChooseAppsAdapter(appInfos, getContext());
        recyclerView.setAdapter(adapter);
//        PackageManager packageManager = getContext().getPackageManager();
//        GridLayout grid = layout.findViewById(R.id.app_chooser_gridlayout);
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager);
//        List<AppInfo> appsWithActivity = new ArrayList<>();
//        for(AppInfo appInfo:appInfos){
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            if(mainActivityIntent != null){
//                appsWithActivity.add(appInfo);
//            }
//        }
//        for(AppInfo appInfo: appsWithActivity){
//            LinearLayout eachItem = (LinearLayout) inflater.inflate(R.layout.choose_apps_item,grid,false);
//            ImageView appIcon = eachItem.findViewById(R.id.app_icon);
//            appIcon.setImageDrawable(appInfo.getIcon());
//            TextView appName = eachItem.findViewById(R.id.app_name);
//            appName.setText(appInfo.getAppName());
//            grid.addView(eachItem);
//        }
//        PackageManager packageManager = getContext().getPackageManager();
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager);
//        List<AppInfo> appsWithActivity = new ArrayList<>();
//        for (AppInfo appInfo : appInfos) {
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            if (mainActivityIntent != null) {
//                appsWithActivity.add(appInfo);
//            }
//        }
//        ChooseAppAdapter adapter = new ChooseAppAdapter(appsWithActivity);
//        GridView gridView = layout.findViewById(R.id.app_chooser_gridview);
//        gridView.setAdapter(adapter);

        return layout;
    }

    //Perform action if any icon is clicked from the Action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectAll:
                //Select all checkboxes in the recyclerview
                this.adapter.selectAllItems();
//                this.isAllAppsSelected = true;
                return true;

            case R.id.deselectAll:
                this.adapter.deselectAllItems();
//                this.isAllAppsSelected = false;
                return true;
        }
        return false;
    }

}