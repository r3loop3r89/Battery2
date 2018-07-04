package com.shra1.battery2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shra1.battery2.R;
import com.shra1.battery2.services.ScreenOnOffRecieverRegisterarService;
import com.shra1.battery2.utils.SharedPreferencesManager;
import com.shra1.battery2.utils.Utils;

import static com.shra1.battery2.utils.Constants.ON_SCREEN_ON_OFF;
import static com.shra1.battery2.utils.Constants.PERIODICALLY;

public class SettingsFragment extends Fragment {
    static SettingsFragment instance = null;
    Context mCtx;
    Intent serviceIntent;

    private CheckBox cbSFStartServiceAutomatically;
    private TextView tvSFServiceStatus;
    private TextView tvBatteryOptimizationStatus;

    private RadioGroup rgSFBatteryDataSaveType;
    private RadioButton rbSFSaveBatteryDataPeriodically;
    private RadioButton rbSFSaveBatteryDataOnScreenOnOff;

    public static SettingsFragment getInstance() {
        if (instance == null) {
            instance = new SettingsFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.settings_fragment, container, false);
        mCtx = container.getContext();
        serviceIntent = new Intent(mCtx, ScreenOnOffRecieverRegisterarService.class);

        initViews(v);

        final SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(mCtx);

        if (sharedPreferencesManager.getSTART_SERVICE_AUTOMATICALLY()) {
            cbSFStartServiceAutomatically.setChecked(true);
            checkServiceRunningStatus();
        } else {
            cbSFStartServiceAutomatically.setChecked(false);
        }

        cbSFStartServiceAutomatically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;
                if (c.isChecked()) {
                    sharedPreferencesManager.setSTART_SERVICE_AUTOMATICALLY(true);
                    checkServiceRunningStatus();
                } else {
                    sharedPreferencesManager.setSTART_SERVICE_AUTOMATICALLY(false);
                    tvSFServiceStatus.setVisibility(View.GONE);
                    mCtx.stopService(serviceIntent);
                }
            }
        });

        checkServiceRunningStatus();

        checkBatteryOptimizationStatus();

        if (sharedPreferencesManager.getBATTERY_DATA_SAVE_TYPE()==PERIODICALLY){
            rbSFSaveBatteryDataPeriodically.setChecked(true);
        }else{
            rbSFSaveBatteryDataOnScreenOnOff.setChecked(true);
        }

        rbSFSaveBatteryDataPeriodically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenOnOffRecieverRegisterarService.getServiceObject().shutdownWorker();
                mCtx.stopService(serviceIntent);
                sharedPreferencesManager.setBATTERY_DATA_SAVE_TYPE(PERIODICALLY);
                mCtx.startService(serviceIntent);
            }
        });

        rbSFSaveBatteryDataOnScreenOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenOnOffRecieverRegisterarService.getServiceObject().shutdownWorker();
                mCtx.stopService(serviceIntent);
                sharedPreferencesManager.setBATTERY_DATA_SAVE_TYPE(ON_SCREEN_ON_OFF);
                mCtx.startService(serviceIntent);
            }
        });
        return v;
    }

    private void checkBatteryOptimizationStatus() {
        PowerManager powerManager = (PowerManager) mCtx.getSystemService(Context.POWER_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager.isIgnoringBatteryOptimizations(mCtx.getPackageName())){
                Spanned spanned = Html.fromHtml("<font color=\"#008141\"><b>Awesome!</b></font>, This application is allowed to run in the background ignoring battery optimization.");
                tvBatteryOptimizationStatus.setText(spanned);
                tvBatteryOptimizationStatus.setOnClickListener(null);
            }else{
                Spanned spanned = Html.fromHtml("<font color=\"RED\">Ohh ooh!</font>, This application requires battery optimization to be <b>Disabled</b> to work properly. Kindly <font color=\"BLUE\"><b>Click here</b></font> to <b>Disable it</b>. Select this app in the following screen and click on <b>Dont Optimize</b>");
                tvBatteryOptimizationStatus.setText(spanned);
                tvBatteryOptimizationStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivityForResult(intent, 321);
                    }
                });
            }
        }else{
            tvBatteryOptimizationStatus.setVisibility(View.GONE);
        }
    }

    private boolean checkServiceRunningStatus() {
        tvSFServiceStatus.setVisibility(View.VISIBLE);
        if (Utils.isMyServiceRunning(mCtx, ScreenOnOffRecieverRegisterarService.class)) {
            Spanned spanned = Html.fromHtml("Service is <font color=\"#008141\">Running</font>");
            tvSFServiceStatus.setText(spanned);
            tvSFServiceStatus.setOnClickListener(null);
            rgSFBatteryDataSaveType.setVisibility(View.VISIBLE);
            return true;
        } else {
            Spanned spanned = Html.fromHtml("Service is <font color=\"RED\">not running</font>, <b>Click to start!</b>");
            tvSFServiceStatus.setText(spanned);
            tvSFServiceStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkServiceRunningStatus()) { } else {
                        mCtx.startService(serviceIntent);
                        rgSFBatteryDataSaveType.setVisibility(View.VISIBLE);
                        checkServiceRunningStatus();
                    }
                }
            });
            rgSFBatteryDataSaveType.setVisibility(View.GONE);
            return false;
        }
    }

    private void initViews(View v) {
        cbSFStartServiceAutomatically = (CheckBox) v.findViewById(R.id.cbSFStartServiceAutomatically);
        tvSFServiceStatus = (TextView) v.findViewById(R.id.tvSFServiceStatus);
        tvSFServiceStatus.setVisibility(View.GONE);
        tvBatteryOptimizationStatus = v.findViewById(R.id.tvBatteryOptimizationStatus);

        rgSFBatteryDataSaveType = (RadioGroup) v.findViewById(R.id.rgSFBatteryDataSaveType);
        rbSFSaveBatteryDataPeriodically = (RadioButton) v.findViewById(R.id.rbSFSaveBatteryDataPeriodically);
        rbSFSaveBatteryDataOnScreenOnOff = (RadioButton) v.findViewById(R.id.rbSFSaveBatteryDataOnScreenOnOff);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==321){
            checkBatteryOptimizationStatus();
        }
    }
}
