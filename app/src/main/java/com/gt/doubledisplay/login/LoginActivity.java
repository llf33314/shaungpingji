package com.gt.doubledisplay.login;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.LoginSignBean;
import com.gt.doubledisplay.http.ApiService;
import com.gt.doubledisplay.http.BaseResponse;
import com.gt.doubledisplay.http.HttpConfig;
import com.gt.doubledisplay.http.HttpResponseException;
import com.gt.doubledisplay.http.retrofit.HttpCall;
import com.gt.doubledisplay.http.rxjava.observable.DialogTransformer;
import com.gt.doubledisplay.http.rxjava.observable.ResultTransformer;
import com.gt.doubledisplay.http.rxjava.observable.SchedulerTransformer;
import com.gt.doubledisplay.http.rxjava.observer.BaseObserver;
import com.gt.doubledisplay.http.socket.PrintSocketService;
import com.gt.doubledisplay.printer.extraposition.PrinterConnectSerivce;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.printer.internal.ESCUtil;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.gt.doubledisplay.web.WebViewDiffDisplayPresentation;
import com.orhanobut.hawk.Hawk;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
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
        init();
    }



    @OnClick({R.id.login_forget_psd, R.id.btn_login})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.login_forget_psd:

                break;

            case R.id.btn_login:
            /*    Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra(GTWebViewFrameLayout.PARAM_URL,"http://canyin.duofriend.com");
                startActivity(intent);*/


              //  ESCUtil.printInternal("12025477484","你尚未成为会员","￥128.00","￥50.00","-￥0.00","-￥0.00","-￥0.00","￥178.00");
             /*   if (test%2==0){
                    PrinterConnectSerivce.printReceiptClicked("100");
                }else{
                    printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                }
                test++;*/
                // printInternal("12025477484","你尚未成为会员","¥128.00","¥50.00","-¥0.00","-¥0.00","-¥0.00","178.00");
                final String account=etAccount.getText().toString().trim();
                final String psd=etPsd.getText().toString().trim();
                if (TextUtils.isEmpty(account)||TextUtils.isEmpty(psd)){
                    ToastUtil.getInstance().showToast("账号密码不能为空");
                    return;
                }

              /*  HttpCall.getApiService()
                        .getSign(account,psd,"double_screen_sign_code_is_ok")
                        .flatMap(new Function<String, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(@NonNull String s) throws Exception {
                                Gson gson=new Gson();
                                LoginSignBean loginSignBean=gson.fromJson(s,LoginSignBean.class);
                                String data=gson.toJson(loginSignBean.getData());
                                Log.d("LoginActivity",data);
                                return HttpCall.getApiService().login(account,psd,data);
                            }
                        })*/
                HttpCall.getApiService().login(account,psd)
                        .compose(LoginActivity.this.<String>bindToLifecycle())
                        .compose(SchedulerTransformer.<String>transformer())
                        .compose(new DialogTransformer().<String>transformer())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull String s) {
                                //这里取数据是否成功
                                //successCallback({"code":"1","msg":"签名验证错误，请检查签名信息"})
                                //{"code":0,"data":{"UserId":42,"style":1,"message":"登录成功","error":"0"}}
                               // {"code":0,"data":{"message":"登录成功","error":"0","style":1,"UserId":3512}}
                               // {"code":0,"data":{"message":"用户名不存在","error":"2"}}

                                try {
                                    JSONObject json=new JSONObject(s);
                                    json=json.getJSONObject("data");
                                    switch (json.getInt("error")){
                                        case 0://请求后台正常
                                            String userId=json.getString("UserId");
                                            MyApplication.USER_ID=userId;

                                            if (cbPsd.isChecked()){
                                                Hawk.put(ACCOUNT,account);
                                                Hawk.put(PSD,psd);
                                            }else{
                                                Hawk.delete(ACCOUNT);
                                                Hawk.delete(PSD);
                                            }

                                            Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                                            intent.putExtra(GTWebViewFrameLayout.PARAM_URL, HttpConfig.DUOFRIEND_LOGIN);
                                            ToastUtil.getInstance().showNewShort("登录成功");
                                            startActivity(intent);
                                            finish();
                                            break;
                                        case 2:
                                            ToastUtil.getInstance().showToast(json.getString("message"));
                                            Hawk.delete(ACCOUNT);
                                            Hawk.delete(PSD);
                                            break;

                                    }
                                } catch (JSONException e) {
                                    ToastUtil.getInstance().showToast("后台数据有误");
                                    Hawk.delete(ACCOUNT);
                                    Hawk.delete(PSD);
                                    e.printStackTrace();
                                }
                                ///每次应该打开首页 如果是登录页面才打开我们登录页面
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Hawk.delete(ACCOUNT);
                                Hawk.delete(PSD);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });


             /*   HttpCall
                        .getApiService()
                        .login(account,psd)
                        .compose(LoginActivity.this.<BaseResponse>bindToLifecycle())
                        .compose(ResultTransformer.<BaseResponse>transformerNoData())
                        .compose(new DialogTransformer().<BaseResponse>transformer())
                        .subscribe(new BaseObserver<BaseResponse>() {
                            @Override
                            protected void onSuccess(BaseResponse baseResponse) {
                                ToastUtil.getInstance().showNewShort("登录成功"+baseResponse.getError()+baseResponse.getMessage());
                                if (cbPsd.isChecked()){
                                    Hawk.put(ACCOUNT,account);
                                    Hawk.put(PSD,psd);
                                }else{
                                    Hawk.delete(ACCOUNT);
                                    Hawk.delete(PSD);
                                }


                                Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                                intent.putExtra(GTWebViewFrameLayout.PARAM_URL,"http://canyin.duofriend.com");
                                startActivity(intent);
                                ///每次应该打开首页 如果是登录页面才打开我们登录页面
                            }

                            @Override
                            protected void onFailed(HttpResponseException responseException) {
                                super.onFailed(responseException);
                                Hawk.delete(ACCOUNT);
                                Hawk.delete(PSD);
                            }
                        });*/
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
        portIntent = new Intent(this, PrinterConnectSerivce.class);
        startService(portIntent);
    }

}
