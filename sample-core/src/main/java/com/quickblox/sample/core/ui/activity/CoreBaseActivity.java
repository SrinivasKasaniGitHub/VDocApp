package com.quickblox.sample.core.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.ErrorUtils;
import com.quickboxdemo.sample.core.R;

import java.lang.reflect.Field;

public class CoreBaseActivity extends AppCompatActivity {
    protected ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();

//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appTxtColor)));

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }



    @SuppressWarnings("unchecked")
    public <T extends View> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    public void setActionBarTitle(int title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appTxtColor)));

        }
    }

    public void setActionBarTitle(CharSequence title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
           // actionBar.setTitle(Html.fromHtml(title,);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appTxtColor)));
        }
    }

    protected void fillField(TextView textView, String value) {
        textView.setText(value);
    }

    protected void showSnackbarError(View rootLayout, @StringRes int resId, QBResponseException e, View.OnClickListener clickListener) {
        ErrorUtils.showSnackbar(rootLayout, resId, e, R.string.dlg_retry, clickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
