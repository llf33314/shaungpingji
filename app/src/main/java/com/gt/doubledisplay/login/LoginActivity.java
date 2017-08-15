package com.gt.doubledisplay.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.printer.internal.BluetoothUtil;
import com.gt.doubledisplay.printer.internal.ESCUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class LoginActivity extends RxAppCompatActivity {

    //打印机连接
    public static Intent portIntent;
    @BindView(R.id.login_forget_psd)
    TextView loginForgetPsd;
    @BindView(R.id.btn_login)
    Button btnLogin;

    int test=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        portIntent = new Intent(this, PrinterConnectSerivce.class);
        startService(portIntent);
    }

    @OnClick({R.id.login_forget_psd, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_forget_psd:
                Intent intent=new Intent(LoginActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                PrinterConnectSerivce.printReceiptClicked("100");
             /*   if (test%2==0){
                    PrinterConnectSerivce.printReceiptClicked("100");
                }else{
                    printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                }
                test++;*/
                // printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                break;
        }
    }

    public int printInternal(String orderId,String memberMsg,String amountMoney,String seatMoney,String memberDiscount,
                             String friendDiscount,String integralDiscount,String practicalMoney){
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {

            return -1;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {

            return 0;
        }

        // 3: Generate a order data
        byte[] data = ESCUtil.getInternalOrder( orderId, memberMsg, amountMoney, seatMoney, memberDiscount,
                friendDiscount, integralDiscount, practicalMoney);
        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
            return 1;
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return  2;
        }
    }
}
