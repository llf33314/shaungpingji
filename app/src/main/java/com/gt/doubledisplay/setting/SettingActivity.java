package com.gt.doubledisplay.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.printer.policy.WeituPrinter;
import com.gt.doubledisplay.update.OnTaskFinishListener;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.commonutil.LogUtils;
import com.gt.doubledisplay.utils.commonutil.PhoneUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.orhanobut.hawk.Hawk;
import com.weitoo.printer.MsPrinter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/25 0025.
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_imei)
    Button settingImei;
    @BindView(R.id.setting_cb_use_printer)
    CheckBox cbPrinter;
    @BindView(R.id.setting_cb_use_money_box)
    CheckBox cbMoneyBox;
    @BindView(R.id.setting_print_money_box)
    LinearLayout moneyBox;
    @BindView(R.id.setting_ll_use_internal_printer)
    LinearLayout usePrinter;

    public static final String DEVICE_SETTING="deviceSetting";
    public static final int DEVICE_SETTING_USE_PRINTER=0x01;
    public static final int DEVICE_SETTING_USE_MONEY_BOX=0x02;

    private long clickTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init(){
        setToolBarTitle("设置");
        //后续要更换
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        settingImei.setText("设备码："+PhoneUtils.getIMEI()+"\t"+PhoneUtils.getIMSI()+"\t"+ANDROID_ID);
        //转16进制 暂时转不转没问题
        int setting= MyApplication.getSettingCode();
        cbPrinter.setChecked((setting&DEVICE_SETTING_USE_PRINTER)!=0);
        cbMoneyBox.setChecked((setting&DEVICE_SETTING_USE_MONEY_BOX)!=0);

        cbPrinter.setOnCheckedChangeListener(new CheckBoxListener());
        cbMoneyBox.setOnCheckedChangeListener(new CheckBoxListener());
        hideBtns();
    }

    private void hideBtns(){
        if (!(MyApplication.getPrinter() instanceof WeituPrinter)){
           // moneyBox.setVisibility(View.GONE);
            usePrinter.setVisibility(View.GONE);
        }
    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.setting_cb_use_printer:
                    if (isChecked){
                        Hawk.put(DEVICE_SETTING,MyApplication.getSettingCode()|DEVICE_SETTING_USE_PRINTER);
                        if (MyApplication.getMsUsbDriver()==null){//都不设置时 不启动这个服务
                            MyApplication.getUsbDriverService();
                        }
                    }else{
                        Hawk.put(DEVICE_SETTING,MyApplication.getSettingCode()&~DEVICE_SETTING_USE_PRINTER);
                    }

                    break;
                case R.id.setting_cb_use_money_box:
                    if (isChecked){
                        Hawk.put(DEVICE_SETTING,MyApplication.getSettingCode()|DEVICE_SETTING_USE_MONEY_BOX);
                        //如果是微兔设备才去打开 usb服务 如果是065设备什么都不做
                        if (MyApplication.getPrinter() instanceof WeituPrinter&&MyApplication.getMsUsbDriver()==null){
                            //都不设置时 不启动这个服务 所以在这里写
                            MyApplication.getUsbDriverService();
                        }
                    }else {
                        Hawk.put(DEVICE_SETTING,MyApplication.getSettingCode()&~DEVICE_SETTING_USE_MONEY_BOX);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @OnClick({R.id.blu_setting, R.id.check_update,R.id.setting_print_test,R.id.setting_print_money_box})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blu_setting:
                Intent intent = new Intent(SettingActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.check_update:
                checkUpdate();
                break;
            case R.id.setting_print_test:
                MyApplication.getPrinter().printTest();
                break;
            case R.id.setting_print_money_box:
                MyApplication.getPrinter().openMoneyBox();
                break;
            default:
                break;
        }
    }

    private void checkUpdate() {
        if (SystemClock.uptimeMillis() - clickTime < 1500){
            return;
        }
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
