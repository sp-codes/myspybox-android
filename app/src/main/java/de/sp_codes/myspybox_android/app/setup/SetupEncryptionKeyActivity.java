package de.sp_codes.myspybox_android.app.setup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.sp_codes.myspybox_android.R;
import de.sp_codes.myspybox_android.app.base.BaseActivity;

public class SetupEncryptionKeyActivity extends BaseActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private KeyGenerationTask generationTask;

    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_encryption_key);
        // Set up the login form.
        EditText userIdView = (EditText) findViewById(R.id.user_id);
        userIdView.setText(getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.USER_ID, null));

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.check_server || id == EditorInfo.IME_ACTION_DONE) {
                    generateKey();
                    return true;
                }
                return false;
            }
        });

        Button checkServer = (Button) findViewById(R.id.check_server_button);
        checkServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                generateKey();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid serverUrl, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void generateKey() {
        if (generationTask != null) {
            return;
        }

        passwordView.setError(null);

        String password = passwordView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            passwordView.requestFocus();
            return;
        }

        showProgress(true);
        generationTask = new KeyGenerationTask(password);
        generationTask.execute((Void) null);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class KeyGenerationTask extends AsyncTask<Void, Void, Boolean> {

        private final String password;
        private String encryptionKey;

        KeyGenerationTask(String password) {
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            this.encryptionKey = "abcd-efgh-ijkl-mnop";

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            generationTask = null;
            showProgress(false);

            if (success) {
                Intent data = new Intent();
                data.putExtra(SetupActivity.ENCRYPTION_KEY, encryptionKey);
                setResult(RESULT_OK, data);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            generationTask = null;
            showProgress(false);
        }
    }
}