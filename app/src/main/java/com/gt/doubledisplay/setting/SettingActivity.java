package com.gt.doubledisplay.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.update.OnTaskFinishListener;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.commonutil.PhoneUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/25 0025.
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_imei)
    Button settingImei;
    private long clickTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolBarTitle("设置");
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        settingImei.setText("设备码："+PhoneUtils.getIMEI()+"\t"+PhoneUtils.getIMSI()+"\t"+ANDROID_ID);
    }

    @OnClick({R.id.blu_setting, R.id.check_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blu_setting:
                Intent intent = new Intent(SettingActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.check_update:
                checkUpdate();
                break;
        }
    }

    private void checkUpdate() {
        if (SystemClock.uptimeMillis() - clickTime < 1500) return;
        clickTime = SystemClock.uptimeMillis();
        UpdateManager updateManager = new UpdateManager(this, "DoubleScreen");
        updateManager.requestUpdate();
        updateManager.setOnTaskFinishListener(new OnTaskFinishListener() {
            @Override
            public void onTaskResult(boolean result) {
                if (!result) {
                    ToastUtil.getInstance().showToast("当前已是最新版本");
                }
            }
        });
    }
}
