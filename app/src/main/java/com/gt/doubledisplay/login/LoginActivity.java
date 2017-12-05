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
import android.widget.Toast;

import com.google.gson.Gson;
import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.bean.LoginSignBean;
import com.gt.doubledisplay.http.HttpResponseException;
import com.gt.doubledisplay.http.retrofit.HttpCall;
import com.gt.doubledisplay.http.rxjava.observable.DialogTransformer;
import com.gt.doubledisplay.http.rxjava.observable.ResultTransformer;
import com.gt.doubledisplay.http.rxjava.observable.SchedulerTransformer;
import com.gt.doubledisplay.http.rxjava.observer.BaseObserver;
import com.gt.doubledisplay.update.UpdateManager;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.AppUtils;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.orhanobut.hawk.Hawk;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.gt.doubledisplay.login.ChooseBusinessDialog.REMEMBER_BUSINESS_CHOOSE;

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


    private static final String ACCOUNT = "login_account";
    private static final String PSD = "login_psd";
    //打印机连接
    public static Intent portIntent;

    Gson gson = new Gson();
    @BindView(R.id.version)
    TextView version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();

        UpdateManager updateManager = new UpdateManager(this, "DoubleScreen");
        updateManager.requestUpdate();
    }

    @OnClick({R.id.login_forget_psd, R.id.btn_login})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.login_forget_psd:

                break;

            case R.id.btn_login:

                final String account = etAccount.getText().toString().trim();
                final String psd = etPsd.getText().toString().trim();
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psd)) {
                    ToastUtil.getInstance().showToast("账号密码不能为空");
                    return;
                }

                HttpCall.getApiService()
                        .getBusId(account, psd)
                        .flatMap(ResultTransformer.<LoginSignBean>flatMap())
                        .flatMap(new Function<LoginSignBean, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(@NonNull LoginSignBean loginSignBean) throws Exception {
                                LoginSignBean.SignBean sign = loginSignBean.getSign();

                                //保存设备信息 给副屏调用
                                //MyApplication.USER_ID= HttpConfig.SOCKET_ANDROID_AUTH_KEY+loginSignBean.getStyle()+"_"+loginSignBean.getUserId();
                                MyApplication.USER_ID = loginSignBean.getUserId() + "";
                                MyApplication.DEVICE_ID = loginSignBean.getEqCode();

                                if (cbPsd.isChecked()) {
                                    Hawk.put(ACCOUNT, account);
                                    Hawk.put(PSD, psd);
                                } else {
                                    Hawk.delete(ACCOUNT);
                                    Hawk.delete(PSD);
                                }
                                //暂时保存
                                MyApplication.setLoginBean(loginSignBean);


                                return HttpCall.getApiService().login(account, psd, gson.toJson(sign));
                            }
                        })
                        .compose(LoginActivity.this.<String>bindToLifecycle())
                        .compose(SchedulerTransformer.<String>transformer())
                        .compose(new DialogTransformer().<String>transformer())
                        .subscribe(new BaseObserver<String>() {
                            @Override
                            protected void onSuccess(String s) {
                                String jsonResult;
                                LoginBean loginBean = null;
                                try {
                                    jsonResult = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                                    loginBean = gson.fromJson(jsonResult, LoginBean.class);
                                } catch (Exception e) {
                                    ToastUtil.getInstance().showToast("后台数据有误");
                                    return;
                                }

                                //没有绑定任何 模块返回 {}
                                if (TextUtils.isEmpty(loginBean.getCode())) {
                                    new NoBindingBusinessDialog(LoginActivity.this, R.style.HttpRequestDialogStyle).show();
                                    return;
                                }

                                if ("0".equals(loginBean.getCode())) {//登录成功

                                    List<LoginBean.ErplistBean> erpList = loginBean.getErplist();

                                    if (erpList != null && erpList.size() > 0) {//有绑定erp
                                        if (erpList.size() == 1) {//只有一个erp时候直接跳转

                                            startErp(erpList, 0);

                                        } else {//多个erp
                                            String erpKey = Hawk.get(REMEMBER_BUSINESS_CHOOSE, "");
                                            if (!TextUtils.isEmpty(erpKey)) {//有选择记住我的选择
                                                for (int i = 0; i < erpList.size(); i++) {
                                                    if (erpKey.equals(erpList.get(i).getItem_key())) {//找到对应的erp跳转
                                                        startErp(erpList, i);
                                                    }
                                                }
                                            } else { //多个erp并且没有记住选择
                                                new ChooseBusinessDialog(LoginActivity.this, R.style.HttpRequestDialogStyle, erpList).show();
                                            }

                                        }

                                    } else {
                                        new NoBindingBusinessDialog(LoginActivity.this, R.style.HttpRequestDialogStyle).show();
                                    }

                                } else {//登录失败
                                    ToastUtil.getInstance().showToast(loginBean.getMsg());
                                    /* Hawk.delete(ACCOUNT);
                                     Hawk.delete(PSD)*/
                                    ;
                                }
                            }

                            @Override
                            protected void onFailed(HttpResponseException responseException) {
                                super.onFailed(responseException);
                                Hawk.delete(ACCOUNT);
                                Hawk.delete(PSD);
                            }
                        });

                break;
            default:
                break;
        }
    }

    private void startErp(List<LoginBean.ErplistBean> erpList, int erpPosition) {
        try {
            String erpUrl = erpList.get(erpPosition).getItem_remark().split(",")[0];
            Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
            intent.putExtra(GTWebViewFrameLayout.PARAM_URL, erpUrl);
            //ToastUtil.getInstance().showNewShort("登录成功");
            startActivity(intent);
            //显示副屏广告
            RxBus.get().post(new DeviceBean(MyApplication.getLoginBean().getEqCode()));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "后台信息配置有误请联系商家", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {

        etPsd.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    btnLogin.performClick();

                    return true;
                }
                return false;
            }

        });

        String account = Hawk.get(ACCOUNT, "");
        String psd = Hawk.get(PSD, "");
        if (!TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }
        if (!TextUtils.isEmpty(psd)) {
            etPsd.setText(psd);
        }
        if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(account)) {
            cbPsd.setChecked(true);
        }
        /*portIntent = new Intent(this, PrinterConnectService.class);
        startService(portIntent);*/
        if (!TextUtils.isEmpty(AppUtils.getAppInfo().getVersionName())){
            version.setText("版本号："+AppUtils.getAppInfo().getVersionName());
        }
    }

}
