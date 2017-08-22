package com.gt.doubledisplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.http.BaseResponse;
import com.gt.doubledisplay.http.retrofit.HttpCall;
import com.gt.doubledisplay.http.rxjava.observable.DialogTransformer;
import com.gt.doubledisplay.http.rxjava.observable.ResultTransformer;
import com.gt.doubledisplay.http.rxjava.observer.BaseObserver;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.sonic.SonicRuntimeImpl;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.gt.doubledisplay.web.GTWebViewFrameLayout.MODE_DEFAULT;
import static com.gt.doubledisplay.web.GTWebViewFrameLayout.MODE_SONIC;

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
    @BindView(R.id.login_account)
    EditText etAccount;
    @BindView(R.id.login_psd)
    EditText etPsd;

    int test=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        portIntent = new Intent(this, PrinterConnectSerivce.class);
        startService(portIntent);
        init();
    }

    @OnClick({R.id.login_forget_psd, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_forget_psd:
                Intent intent=new Intent(LoginActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                break;
            
            case R.id.btn_login:
              //  PrinterConnectSerivce.printReceiptClicked("100");
               // ESCUtil.printInternal("12025477484","你尚未成为会员","￥128.00","￥50.00","-￥0.00","-￥0.00","-￥0.00","￥178.00");
             /*   if (test%2==0){
                    PrinterConnectSerivce.printReceiptClicked("100");
                }else{
                    printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                }
                test++;*/
                // printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                String account=etAccount.getText().toString().trim();
                String psd=etPsd.getText().toString().trim();
                if (TextUtils.isEmpty(account)||TextUtils.isEmpty(psd)){
                    ToastUtil.getInstance().showToast("账号密码不能为空");
                    return;
                }
                /*if (test!=0){
                    Intent intent2=new Intent(LoginActivity.this, WebViewActivity.class);
                    intent2.putExtra(GTWebViewFrameLayout.PARAM_MODE,GTWebViewFrameLayout.MODE_DEFAULT);
                    intent2.putExtra(GTWebViewFrameLayout.PARAM_URL,"http://deeptel.com.cn/user/toIndex.do?setType=index");
                    startActivity(intent2);
                    return;
                }*/
                HttpCall
                        .getApiService()
                        .login(account,psd)
                        .compose(LoginActivity.this.<BaseResponse>bindToLifecycle())
                        .compose(ResultTransformer.<BaseResponse>transformerNoData())
                        .compose(new DialogTransformer().<BaseResponse>transformer())
                        .subscribe(new BaseObserver<BaseResponse>() {
                            @Override
                            protected void onSuccess(BaseResponse baseResponse) {
                                ToastUtil.getInstance().showNewShort("登录成功"+baseResponse.getError()+baseResponse.getMessage());
                                Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                                intent.putExtra(GTWebViewFrameLayout.PARAM_MODE,GTWebViewFrameLayout.MODE_DEFAULT);
                                intent.putExtra(GTWebViewFrameLayout.PARAM_URL,"http://deeptel.com.cn/user/toIndex.do?setType=index");
                                startActivity(intent);
                                test++;
                                ///每次应该打开首页 如果是登录页面才打开我们登录页面
                            }
                        });
                break;
        }
    }
    private void init() {
        // init sonic engine
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
    }

}
