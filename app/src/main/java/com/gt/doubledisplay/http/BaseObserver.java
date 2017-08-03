package com.gt.doubledisplay.http;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.Log;

import com.google.gson.Gson;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 预处理返回Code,网络异常
 */

public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {

    //private Disposable disposable;//按返回键 取消对话框 取消订阅 取消请求

    private final String TAG=BaseObserver.class.getSimpleName();
    private Context mContext;

    private static Gson gson = new Gson();

    private boolean showDialog;

    private final int RESPONSE_CODE_FAILED = -2;  //返回数据失败,严重的错误

    private int errorCode;
    private String errorMsg = "未知的错误！";

    //真正需要的数据
    public abstract void onSuccess (T data);

    public BaseObserver(Context context,boolean showDialog){
        this.showDialog=showDialog;

        this.mContext=context;
    }

    public  BaseObserver(Context context){
        this(context,false);
    }

    /**
     * 可复写
     */
    @CallSuper
    @Override
    public void onSubscribe(@NonNull Disposable d) {
       // this.disposable=d;
        if (showDialog){
            showProgressDialog(d);
        }
    }
    /**
     * 可复写
     */
    @Override
    public void onComplete() {

    }

    /**
     * 显示网络加载对话框
     */
    private void showProgressDialog(Disposable d){
        HttpRequestDialog.getInstance().show(d);
    }

    private void dismissProgressDialog(){
        HttpRequestDialog.getInstance().dismiss();
    }

    @Override
    public void onNext( BaseResponse<T> baseResponse) {
        dismissProgressDialog();
        if (baseResponse.getCode()==HttpConfig.SUCCESS_CODE){
            onSuccess(baseResponse.getData());
            Log.i(TAG,"baseResponse.getData()="+baseResponse.getData());
        }else{
            if (baseResponse!=null&&baseResponse.getMsg()!=null){
                onFailure(baseResponse.getCode(),baseResponse.getMsg().toString());
            }else{
                ToastUtil.getInstance().showToast("数据有误");
            }

        }
    }

    /**
     * 网络、服务器异常,暂时只Toast
     */
    @Override
    public void onError(@NonNull Throwable e) {
        dismissProgressDialog();

        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            errorCode = httpException.code();
            errorMsg = httpException.getMessage();
            getErrorMsg(httpException);
        } else if (e instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "服务器响应超时";
        } else if (e instanceof ConnectException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "连接服务器异常";
        } else if (e instanceof RuntimeException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "程序出错";
            Log.e(TAG,e.getMessage());
        } else if (e instanceof UnknownHostException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "无网络连接，请检查网络是否开启";
        } else if (e instanceof UnknownServiceException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "未知的服务器错误";
        } else if (e instanceof IOException) {  //飞行模式等
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "没有网络，请检查网络连接";
        }
        onFailure(errorCode, errorMsg);  //

    }

    private final void getErrorMsg(HttpException httpException) {

        try {
            BaseResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), BaseResponse.class);
            if (null != errorResponse) {
                errorCode = errorResponse.getCode();
                errorMsg = errorResponse.getMsg().toString();
            } else {
                errorCode = RESPONSE_CODE_FAILED;
                errorMsg = "ErrorResponse is null";
            }
        } catch (Exception jsonException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = "http请求错误Json 信息异常";
            Log.e(TAG,jsonException.getMessage());
            jsonException.printStackTrace();
        }
    }


    /**
     * 预处理服务器返回失败的code
     */
    @CallSuper  //if overwrite,you should let it run.
    public void onFailure(int code, String msg) {
        if (code == HttpConfig.NOT_TOKEN && mContext != null) {
            goLoginActivity();
        } else {
            disposeErrCode(code, msg);
        }
    }
    public void disposeErrCode(int code ,String msg){
        Log.e(TAG,"errorCode="+code+"\n"+msg);
        ToastUtil.getInstance().showToast(msg);
    }

    /**
     * 跳转到登录页面
     */
    private void goLoginActivity(){

    }
}
