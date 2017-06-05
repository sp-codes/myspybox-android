package de.sp_codes.myspybox_android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.sp_codes.myspybox_android.app.base.BaseActivity;

/**
 * Created by samuel on 24.03.17.
 */

public class MainServicesStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getSharedPreferences(BaseActivity.PREFERENCES, Context.MODE_PRIVATE).getBoolean(BaseActivity.PreferenceKeys.IS_SETUP_COMPLETE, BaseActivity.PreferenceDefaults.IS_SETUP_COMPLETE)) {
            context.startService(new Intent(context, AppUsageTrackingService.class));
        }
    }
}