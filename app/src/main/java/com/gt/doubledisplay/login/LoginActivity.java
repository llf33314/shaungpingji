package com.gt.doubledisplay.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.bean.LoginSignBean;
import com.gt.doubledisplay.http.ApiService;
import com.gt.doubledisplay.http.BaseResponse;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.HttpResponseException;
import com.gt.doubledisplay.http.retrofit.HttpCall;
import com.gt.doubledisplay.http.rxjava.observable.DialogTransformer;
import com.gt.doubledisplay.http.rxjava.observable.ResultTransformer;
import com.gt.doubledisplay.http.rxjava.observer.BaseObserver;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.ScreenUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.orhanobut.hawk.Hawk;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class LoginActivity extends RxAppCompatActivity {

    @BindView(R.id.login_forget_psd)
    TextView loginForgetPsd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.login_account)
    EditText etAccount;
    @BindView(R.id.login_psd)
    EditText etPsd;
    @BindView(R.id.login_cb_psd)
    CheckBox cbPsd;

    private static final String ACCOUNT="login_account";
    private static final String PSD="login_psd";
    //打印机连接
    public static Intent portIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        float a= ScreenUtils.getDensity();
        init();

        //连接socket  暂时这么写 如果是登录页面就不启动
        //UpdateManager updateManager=new UpdateManager(this,"DoubleScreen");
        //updateManager.requestUpdate();
    }

    @OnClick({R.id.login_forget_psd, R.id.btn_login})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.login_forget_psd:

                break;

            case R.id.btn_login:

                /*if (true){
                    new ChooseBusinessDialog(LoginActivity.this,R.style.HttpRequestDialogStyle).show();
                    return;
                }*/

                final String account=etAccount.getText().toString().trim();
                final String psd=etPsd.getText().toString().trim();
                if (TextUtils.isEmpty(account)||TextUtils.isEmpty(psd)){
                    ToastUtil.getInstance().showToast("账号密码不能为空");
                    return;
                }

                //暂时去掉 等session共享再放上去
               HttpCall.getApiService()
                        .getSign(account,psd,"double_screen_sign_code_is_ok")
                        .flatMap(new Function<BaseResponse<LoginSignBean>, ObservableSource<BaseResponse<LoginBean>>>() {
                            @Override
                            public ObservableSource<BaseResponse<LoginBean>> apply(@NonNull BaseResponse<LoginSignBean> loginSignBeanBaseResponse) throws Exception {

                                return  HttpCall.getApiService().login(account,psd,loginSignBeanBaseResponse.getData().getSign());}
                        })
               /* HttpCall.getApiService()
                        .login(account,psd)*/
                        .flatMap(ResultTransformer.<LoginBean>flatMap())//这里会去处理 非成功的code
                        .flatMap(new Function<LoginBean, ObservableSource<BaseResponse<DeviceBean>>>(){
                            @Override
                            public ObservableSource<BaseResponse<DeviceBean>> apply(@NonNull LoginBean loginBean) throws Exception {
                                if ("0".equals(loginBean.getError())){//登录成功
                                    MyApplication.USER_ID= HttpConfig.SOCKET_ANDROID_AUTH_KEY+loginBean.getStyle()+"_"+loginBean.getUserId();
                                    if (cbPsd.isChecked()){
                                        Hawk.put(ACCOUNT,account);
                                        Hawk.put(PSD,psd);
                                    }else{
                                        Hawk.delete(ACCOUNT);
                                        Hawk.delete(PSD);
                                    }
                                }
                                return HttpCall.getApiService().getDeviceId(String.valueOf(loginBean.getUserId()));
                            }
                        })
                        .compose(LoginActivity.this.<BaseResponse<DeviceBean>>bindToLifecycle())
                        .compose(ResultTransformer.<DeviceBean>transformer())
                        .compose(new DialogTransformer().<DeviceBean>transformer())
                        .subscribe(new BaseObserver<DeviceBean>() {
                            //{"code":0,"data":{"UserId":42,"style":1,"message":"登录成功","error":"0"}}
                            // {"code":0,"data":{"message":"登录成功","error":"0","style":1,"UserId":3512}}
                            // {"code":0,"data":{"message":"用户名不存在","error":"2"}}

                            @Override
                            protected void onSuccess(DeviceBean deviceBean) {
                                MyApplication.DEVICE_ID=deviceBean.getEqCode();
                                Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                                intent.putExtra(GTWebViewFrameLayout.PARAM_URL, HttpConfig.DUOFRIEND_XCM);
                                //ToastUtil.getInstance().showNewShort("登录成功");
                                startActivity(intent);
                                //显示副屏广告
                                RxBus.get().post(deviceBean);
                                finish();
                            }

                            @Override
                            protected void onFailed(HttpResponseException responseException) {
                                Hawk.delete(ACCOUNT);
                                Hawk.delete(PSD);
                                if (responseException.getCode()==9999){
                                    ToastUtil.getInstance().showToast("账号密码错误");
                                }else{
                                    super.onFailed(responseException);
                                }
                            }
                        });

                break;
        }
    }
    private void init() {

        etPsd.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    btnLogin.performClick();

                    return true;
                }
                return false;
            }

        });

        String account= Hawk.get(ACCOUNT,"");
        String psd=Hawk.get(PSD,"");
        if (!TextUtils.isEmpty(account)){
            etAccount.setText(account);
        }
        if (!TextUtils.isEmpty(psd)){
            etPsd.setText(psd);
        }
        if(!TextUtils.isEmpty(psd)||!TextUtils.isEmpty(account)){
            cbPsd.setChecked(true);
        }
        portIntent = new Intent(this, PrinterConnectSerivce.class);
        //0911 新设备暂时屏蔽 否则打开佳博usb端口会报错
        startService(portIntent);
    }

}
