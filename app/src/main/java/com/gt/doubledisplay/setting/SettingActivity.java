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

import com.google.gson.Gson;
import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.printer.extraposition.PrintESCOrTSCUtil;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
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

    private Gson gson;
    public static final String DEVICE_SETTING="deviceSetting";
    public static final int DEVICE_SETTING_USE_PRINTER=0x01;
    public static final int DEVICE_SETTING_USE_MONEY_BOX=0x02;

    private long clickTime;
    private String printTestString="{\"cashier\":\"\",\"consumption_money\":62.32,\"fansCurrency_deduction\":0,\"integral_deduction\":0,\"member_deduction\":0,\"menus\":[{\"commnt\":\"\",\"menu_no\":\"1002\",\"money\":10,\"name\":\"红烧鱼\",\"norms\":\"\",\"num\":1,\"original_price\":10},{\"commnt\":\"\",\"money\":11,\"name\":\"叉烧包\",\"norms\":\"\",\"num\":1,\"original_price\":11},{\"commnt\":\"\",\"menu_no\":\"1004\",\"money\":38,\"name\":\"咸菜炒猪大肠\",\"norms\":\"\",\"num\":1,\"original_price\":38},{\"commnt\":\"\",\"money\":3.32,\"name\":\"鱼粥\",\"norms\":\"加1  小份  黑色\",\"num\":1,\"original_price\":3.32}],\"order_code\":\"DD1508895434927\",\"order_id\":\"A00005\",\"order_time\":\"2017-10-25 09:37:14\",\"payWay\":\"现金支付\",\"pay_money\":62.32,\"pay_time\":\"2017-10-25 09:37:16\",\"pay_type\":1,\"print_type\":1,\"qrUrl\":\"http://canyin.duofriend.com//simple/79B4DE7C/orderDetailtoxp.do?orderId=3459\",\"remark\":\"\",\"result\":1,\"shop_adress\":\"广东省深圳市南山区兰光科技园C座513\",\"shop_name\":\"谷通科技\",\"shop_phone\":\"0755-26609632\",\"yhq_deduction\":0}";

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
                        if (MyApplication.getMsUsbDriver()==null){//都不设置时 不启动这个服务 所以在这里写
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
                printTestStoreOrder();
                break;
            case R.id.setting_print_money_box:
                MsPrinter.openMoneyBox();
                break;
            default:
                break;
        }

    }

    public void printTestStoreOrder(){
        if(gson==null){
            gson=new Gson();
        }
        StoreOrderBean order=gson.fromJson(printTestString,StoreOrderBean.class);
        //打印不干胶
        PrintESCOrTSCUtil.printStoreXCM(order.getOrder_id(),order.getMenus());
        //微兔打印内置打印机
        if (MyApplication.getPrinter()!=null){
            MyApplication.getPrinter().printStoreOrder(order);
        }else{
            ToastUtil.getInstance().showToast("设备非打印机设备",5000);
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
