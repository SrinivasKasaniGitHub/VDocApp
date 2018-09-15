package com.quickboxdemo.activities;

import android.content.Intent;
import android.os.Bundle;

import com.quickblox.sample.core.ui.activity.CoreSplashActivity;
import com.quickblox.sample.core.utils.SharedPrefsHelper;

import com.quickblox.users.model.QBUser;
import com.quickboxdemo.R;
import com.quickboxdemo.services.CallService;
import com.quickboxdemo.utils.Consts;

import java.util.HashMap;

public class SplashActivity extends CoreSplashActivity {

    private SharedPrefsHelper sharedPrefsHelper;
    String UserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser()) {
            startLoginService(sharedPrefsHelper.getQbUser());
            HashMap<String, String> user = sharedPrefsHelper.getDocUser();
            UserType = user.get(SharedPrefsHelper.KEY_DOC_USER);
            if ("Doctor".equals(UserType)){
                Intent intent_Doctor_Availability = new Intent(getApplicationContext(), OpponentsActivity.class);
                intent_Doctor_Availability.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent_Doctor_Availability.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, false);
                startActivity(intent_Doctor_Availability);
                finish();
            }else {
                startOpponentsActivity();
            }
            return;
        }

        if (checkConfigsWithSnackebarError()) {
            proceedToTheNextActivityWithDelay();
        }
    }

    @Override
    protected String getAppName() {
        return getString(R.string.splash_app_title);
    }

    @Override
    protected void proceedToTheNextActivity() {
        LoginActivity.start(this);
        finish();
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }

    private void startOpponentsActivity() {
        Intent intent_DashBoard=new Intent(getApplicationContext(),DashBoardActivity.class);
        startActivity(intent_DashBoard);
        finish();
    }
}