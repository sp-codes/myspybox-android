package de.sp_codes.myspybox_android.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Comparator;
import java.util.List;

import de.sp_codes.myspybox_android.app.setup.SetupActivity;

import static java.util.Collections.sort;

/**
 * Created by samuel on 24.03.17.
 */

public class AppUsageTrackingService extends Service {
    private static final String TAG = "AppUsageTrackingService";

    private CheckCurrentActivityThread checkCurrentActivityThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkCurrentActivityThread = new CheckCurrentActivityThread(this);
        checkCurrentActivityThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkCurrentActivityThread != null) {
            checkCurrentActivityThread.interrupt();
        }
    }

    // TODO include in persistence
    private void getAllApps() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Package :" + packageInfo.packageName);
            Log.d(TAG, "Label : " + packageInfo.loadLabel(pm));
        }
    }

    private class CheckCurrentActivityThread extends Thread {
        ActivityManager activityManager = null;
        Context context = null;

        public CheckCurrentActivityThread(Context context) {
            this.context = context;
            this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }

        public void run() {
            Looper.prepare();

            String current = null;

            while (!isInterrupted()) {
                String currentRunningAppId = getTopAppId();

                if (currentRunningAppId != null && !currentRunningAppId.equals(current)) {
                    if (current != null)
                        Log.d(TAG, "Stop " + current);
                    current = currentRunningAppId;
                    Log.d(TAG, "Start " + current);
                }
            }
            Looper.loop();
        }

        private String getTopAppId() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
                if (null != appTasks && !appTasks.isEmpty()) {
                    return appTasks.get(0).topActivity.getPackageName();
                }
            } else {
                UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis());
                if (stats == null || stats.isEmpty()) {
                    Intent intent = new Intent(context, SetupActivity.class);
                    intent.putExtra(SetupActivity.PERMISSION_MISSING, true);
                    startActivity(intent);
                    interrupt();
                    AppUsageTrackingService.this.stopSelf();
                } else {
                    sort(stats, new Comparator<UsageStats>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public int compare(UsageStats o1, UsageStats o2) {
                            long x = o1.getLastTimeUsed();
                            long y = o2.getLastTimeUsed();
                            return (x < y) ? 1 : ((x == y) ? 0 : -1);
                        }
                    });
                    return stats.get(0).getPackageName();
                }
            }
            return null;
        }
    }
}