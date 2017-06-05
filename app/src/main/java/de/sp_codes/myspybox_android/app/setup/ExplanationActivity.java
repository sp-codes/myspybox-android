package de.sp_codes.myspybox_android.app.setup;

import android.os.Bundle;
import android.view.View;

import de.sp_codes.myspybox_android.R;
import de.sp_codes.myspybox_android.app.base.BaseActivity;

public class ExplanationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);
    }

    public void onSetupClicked(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
