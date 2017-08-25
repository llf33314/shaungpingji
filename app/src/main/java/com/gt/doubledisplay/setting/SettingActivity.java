package com.gt.doubledisplay.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/25 0025.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolBarTitle("设置");
    }

    @OnClick({R.id.blu_setting, R.id.check_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blu_setting:
                Intent intent=new Intent(SettingActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.check_update:
                break;
        }
    }
}
