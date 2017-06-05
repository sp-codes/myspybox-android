package de.sp_codes.myspybox_android.app.setup;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.sp_codes.myspybox_android.R;
import de.sp_codes.myspybox_android.app.base.BaseActivity;

import static java.util.Arrays.asList;

public class SetupPermissionsActivity extends BaseActivity {
    private final List<? extends PermissionHolder> ALL_PERMISSIONS = asList(
            new PermissionHolder(R.string.permission_usage_stats, Build.VERSION_CODES.LOLLIPOP_MR1) {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                boolean isChecked() {
                    final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                    final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, System.currentTimeMillis());
                    return !queryUsageStats.isEmpty();
                }

                @Override
                void click() {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                }
            }
    );

    private ListView permissionList;
    private List<PermissionHolder> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_permissions);

        permissions = new ArrayList<>();
        for (PermissionHolder permission : ALL_PERMISSIONS) {
            if (android.os.Build.VERSION.SDK_INT >= permission.getRequiredApiLevel()) {
                permissions.add(permission);
            }
        }

        permissionList = (ListView) findViewById(R.id.permission_list);

        permissionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                permissions.get(position).click();
            }
        });
        setTheme(R.style.AppTheme_LightText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionList.setAdapter(new PermissionHolderAdapter(this, permissions));
        boolean enabled = true;
        for (PermissionHolder permission : permissions) {
            if (!permission.isChecked()) {
                enabled = false;
            }
        }
        findViewById(R.id.startup_finish).setEnabled(enabled);
    }

    public void onFinishClicked(View view) {
        setResult(RESULT_OK);
        finish();
    }

    private static abstract class PermissionHolder {
        private int titleResId;
        private int requiredApiLevel;

        PermissionHolder(int titleResId, int requiredApiLevel) {
            this.titleResId = titleResId;
            this.requiredApiLevel = requiredApiLevel;
        }

        int getTitleResId() {
            return titleResId;
        }

        int getRequiredApiLevel() {
            return requiredApiLevel;
        }

        abstract boolean isChecked();

        void click() {
        }
    }

    private class PermissionHolderAdapter extends ArrayAdapter<SwitchPreference> {
        PermissionHolderAdapter(@NonNull Context context, List<PermissionHolder> items) {
            super(context, 0);
            for (PermissionHolder item : items) {
                SwitchPreference switchPreference = new SwitchPreference(context);
                switchPreference.setTitle(item.getTitleResId());
                switchPreference.setChecked(item.isChecked());
                add(switchPreference);
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getItem(position).getView(convertView, parent);
        }
    }
}
