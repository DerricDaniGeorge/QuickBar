package com.derric.quickbar.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.derric.quickbar.MainActivity;
import com.derric.quickbar.QuickBarManager;
import com.derric.quickbar.R;
import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;


public class QuickBarService extends Service {

    public static final int NOTIFICATION_ID = 988321;
    private QuickBarManager mQuickBarManager;
    public static final String NOTIFICATION_CHANNEL_ID = "com.derric.quickbar";


    public QuickBarService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //If service is already running, then don't create a new service
        if (mQuickBarManager != null) {
            return START_STICKY;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        //Ask Android OS to provide window service, you can ask for whatever service you required
        // like Wifi service,notification service..etc
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        //Load user settings
        QuickBarManager.Settings userSettings = loadUserSettings(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout linearLayout;
        if (userSettings.quickbarChooseSide.equals(AppConstants.RIGHT)) {
            linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_quickbar_right, null, false);
        } else {
            linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_quickbar_left, null, false);
        }

        mQuickBarManager = new QuickBarManager(this);
        ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) intent.getSerializableExtra("appInfos");
//        System.out.println("Appinfo size in service:" + appInfos.size());
        //Add the quickbar to screen
        mQuickBarManager.addToWindow(linearLayout, userSettings, appInfos);
        //Android OS Oreo or above requires Notificaition channel needs to be created to start
        //foreground service

        //Start the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, showNotification(this, getNotificationChannel()));
        } else {
            startForeground(NOTIFICATION_ID, showNotification(this, NOTIFICATION_CHANNEL_ID));
        }
        return START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getNotificationChannel() {
        String CHANNEL_NAME = "My foreground service";
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return NOTIFICATION_CHANNEL_ID;
    }

    /**
     * Show notification to user that a foreground service is running
     *
     * @param context
     * @return
     */
    private Notification showNotification(Context context, String channelID) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        //Show the notification as soon as the service starts
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.quickbar_logo1_rounded);
        builder.setContentTitle("QuickBar: Active");
        builder.setContentText("Tap here to open QuickBar settings");
        //OnGoing notification cannot be dismissed by user.
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        //When user clicks the notification , open the app
        //Todo : Even if the app is opened, if we click the notification , it will again open the app. Need to fix it.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    //This is onDestroy callBack method of Service class
    @Override
    public void onDestroy() {
        //When the service is destroyed, remove the Quickbar from the screen
        if (mQuickBarManager != null) {
            mQuickBarManager.removeAllQuickBarsFromWindow();
            // Make the window manager null, so that when the user press the lauch quickbar button
            //foreground service will start again if it is destroyed
            mQuickBarManager = null;
        }
        super.onDestroy();
    }

    public QuickBarManager.Settings loadUserSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        System.out.println("settings are --===================>" + preferences.getAll());
        final QuickBarManager.Settings settings = new QuickBarManager.Settings();
        settings.useTransparentBackground = preferences.getBoolean("transparentBar", false);
        settings.showAppsInAscendingOrder = preferences.getBoolean("sortApps", false);
        settings.autoStartAppOnBoot = preferences.getBoolean("autostart", false);
        settings.hideQuickBarOnAppLaunch = preferences.getBoolean("closeQuickBar", false);
        settings.hideQuickBarLogo = preferences.getBoolean("hideLogo", false);
        settings.quickbarChooseSide = preferences.getString("chooseSide", AppConstants.LEFT);
        settings.quickbarChoosePosition = preferences.getString("choosePosition", AppConstants.CENTER);
        settings.selectedApps = preferences.getStringSet("selectedApps", null);
        settings.wasAllAppsSelected = preferences.getBoolean("wasAllAppsSelected", false);
        settings.quickbarTransparency = preferences.getInt("quickbarTransparency", 100);
        settings.hideIconTransparency = preferences.getInt("hideIconTransparency", 100);
        settings.showIconTransparency = preferences.getInt("showIconTransparency", 100);
        settings.appIconSize = preferences.getInt("appIconSize", 35);
        settings.showIconSize = preferences.getInt("showIconSize", 25);
        settings.hideIconSize = preferences.getInt("hideIconSize", 25);
        settings.hideQuickBarSeconds = preferences.getInt("hideQuickBarSeconds", 5);
        settings.showIconChooseSide = preferences.getString("showIconChooseSide", AppConstants.LEFT);
        settings.showIconChoosePosition = preferences.getString("showIconChoosePosition", AppConstants.CENTER);
        settings.hideIconChoosePosition = preferences.getString("hideIconChoosePosition", AppConstants.CENTER);
        settings.quickBarColor = preferences.getInt("quickBarColor", R.color.google_sheet_green);
        settings.vibrateAppIsLaunched = preferences.getBoolean("vibrateAppIsLaunched", true);
        settings.vibrateShowIconPressed = preferences.getBoolean("vibrateShowIconPressed", false);
        settings.vibrateHideIconPressed = preferences.getBoolean("vibrateHideIconPressed", false);
        return settings;
    }
}
