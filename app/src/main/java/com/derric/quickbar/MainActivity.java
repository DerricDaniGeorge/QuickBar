package com.derric.quickbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.fragments.MainMenu;
import com.derric.quickbar.models.AppInfo;
import com.derric.quickbar.services.QuickBarService;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    public static List<AppInfo> appInfos;
    Future<List<AppInfo>> future;
    private MainMenu mainMenu;
    private boolean isSettingsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        setTheme(R.style.Base_Theme_AppCompat);
        //When the app is started, fetch the app details behind the scene.
        //As it has nothing to do with view, we can use java thread executor
        //If already fetched don't fetch again, This null check is added to avoid fetching data which happens when restoring the app after minimising it
        if (appInfos == null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            future = executorService.submit(new Callable<List<AppInfo>>() {
                @Override
                public List<AppInfo> call() {
                    PackageManager packageManager = getPackageManager();
                    appInfos = QuickBarManager.getAllInstalledApps(packageManager, getApplicationContext());
                    return appInfos;
                }
            });
        }
        super.onCreate(savedInstanceState);
        //Set main activity layout
        setContentView(R.layout.activity_main);

//        Bundle extras = getIntent().getExtras();
//        if(extras != null){
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(AppConstants.APP_INFOS, extras.getSerializable(AppConstants.APP_INFOS));
//            mainMenu.setArguments(bundle);
//        }
        //Add the main menu fragment to the activity layout

        Bundle bundle = new Bundle();
        ArrayList<AppInfo> apps = new ArrayList<>();
        if (appInfos == null) {
            try {
                appInfos = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        apps.addAll(appInfos);
        bundle.putSerializable(AppConstants.APP_INFOS, apps);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        mainMenu = new MainMenu();
        mainMenu.setArguments(bundle);
        transaction.add(R.id.container, mainMenu);
        transaction.commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener settingsChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //If any settings got changed, then do below:
                isSettingsChanged = true;
//                Log.d("SettingsMenu","settingsChagned");
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(settingsChangedListener);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        //If we are in the settings fragment
        if (count == 1 && isSettingsChanged) {
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//            alert.setMessage("To reflect the changes, you need to stop and launch the QuickBar again.");
//            alert.setPositiveButton("RELAUNCH", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent stopIntent = new Intent(alert.getContext(), QuickBarService.class);
//                    //Get the activity and call stop service
//                    alert.getContext().stopService(stopIntent);
//                    finish();
//                }
//            });
//            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //Go back to previous fragment
//                    fragmentManager.popBackStack();
//                }
//            });
//            alert.show();
            Toast.makeText(this,"Please stop and re-launch quickbar if there is any changes in settings",Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}