package de.sp_codes.myspybox_android.app.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.sp_codes.myspybox_android.R;
import de.sp_codes.myspybox_android.app.base.BaseActivity;
import de.sp_codes.myspybox_android.service.MainServicesStarter;

public class SetupActivity extends BaseActivity {

    private static final int ACTIVITY_EXPLANATION = 1;
    private static final int ACTIVITY_SETUP_SERVER = 2;
    private static final int ACTIVITY_SETUP_ENCRYPTION_KEY = 3;
    private static final int ACTIVITY_SETUP_PERMISSIONS = 4;

    public static final String SERVER_URL = "server-url";
    public static final String USER_ID = "user-id";
    public static final String ENCRYPTION_KEY = "encryption-key";
    public static final String PERMISSION_MISSING = "permission-missing";
    private static final String ACTIVITY_STARTED = "activity-started";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setResult(RESULT_CANCELED);
        if (getIntent().getBooleanExtra(PERMISSION_MISSING, false)) {
            getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putBoolean(PreferenceKeys.IS_SETUP_COMPLETE, false).apply();
        }
        if (savedInstanceState == null || !savedInstanceState.getBoolean(ACTIVITY_STARTED, false)) {
            showNextActivity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ACTIVITY_STARTED, true);
        super.onSaveInstanceState(outState);
    }

    public void onGetStartedClicked(View view) {
        getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putBoolean(PreferenceKeys.GET_STARTED_SHOWN, true).apply();
        showNextActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_EXPLANATION:
                getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putBoolean(PreferenceKeys.EXPLANATION_SHOWN, true).apply();
                break;
            case ACTIVITY_SETUP_SERVER:
                getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.SERVER_URL, data.getStringExtra(SERVER_URL)).apply();
                getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.USER_ID, data.getStringExtra(USER_ID)).apply();
                break;
            case ACTIVITY_SETUP_ENCRYPTION_KEY:
                getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.ENCRYPTION_KEY, data.getStringExtra(ENCRYPTION_KEY)).apply();
                break;
            case ACTIVITY_SETUP_PERMISSIONS:
                getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putBoolean(PreferenceKeys.IS_SETUP_COMPLETE, true).apply();
                sendBroadcast(new Intent(this, MainServicesStarter.class));
                setResult(RESULT_OK);
                finish();
                break;
        }
        showNextActivity();
    }

    private void showNextActivity() {
        if (!getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.GET_STARTED_SHOWN, PreferenceDefaults.GET_STARTED_SHOWN)) {
            return;
        }
        if (!getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.EXPLANATION_SHOWN, PreferenceDefaults.EXPLANATION_SHOWN)) {
            startActivityForResult(new Intent(this, ExplanationActivity.class), ACTIVITY_EXPLANATION);
            return;
        }
        if (getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.USER_ID, null) == null) {
            startActivityForResult(new Intent(this, SetupServerActivity.class), ACTIVITY_SETUP_SERVER);
            return;
        }
        if (getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.ENCRYPTION_KEY, null) == null) {
            startActivityForResult(new Intent(this, SetupEncryptionKeyActivity.class), ACTIVITY_SETUP_ENCRYPTION_KEY);
            return;
        }
        if (!getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SETUP_COMPLETE, PreferenceDefaults.IS_SETUP_COMPLETE)) {
            startActivityForResult(new Intent(this, SetupPermissionsActivity.class), ACTIVITY_SETUP_PERMISSIONS);
        }
    }
}
