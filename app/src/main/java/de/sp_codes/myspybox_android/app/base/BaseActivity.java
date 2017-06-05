package de.sp_codes.myspybox_android.app.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by samuel on 01.06.17.
 */

public class BaseActivity extends AppCompatActivity {
    public static final String PREFERENCES = "preferences";
    public class PreferenceKeys {
        public static final String GET_STARTED_SHOWN = "get_started_shown";
        public static final String EXPLANATION_SHOWN = "explanation_shown";
        public static final String SERVER_URL = "server_url";
        public static final String USER_ID = "user_id";
        public static final String ENCRYPTION_KEY = "encryption_key";
        public static final String IS_SETUP_COMPLETE = "setup_complete";
    }

    public class PreferenceDefaults {
        public static final boolean GET_STARTED_SHOWN = false;
        public static final boolean EXPLANATION_SHOWN = false;
        public static final String SERVER_URL = "https://server.myspybox.io";
        public static final boolean IS_SETUP_COMPLETE = false;
    }
}