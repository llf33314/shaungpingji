package com.gt.doubledisplay.http.rxjava.observable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.gt.doubledisplay.http.HttpRequestDialog;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Description:
 * Company:
 * Email:bjxm2013@163.com
 * Created by Devin Sun on 2017/4/3.
 */
public class DialogTransformer {

    //这个对话框、网络加载能否取消  默认能
    private boolean cancelable=true;

    public DialogTransformer() {
        this(true);
    }

    public DialogTransformer(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public <T> ObservableTransformer<T, T> transformer() {
        return new ObservableTransformer<T, T>() {
            private HttpRequestDialog httpRequestDialog;
            @Override
            public ObservableSource<T> apply(final Observable<T> upstream) {

                return  upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull final Disposable disposable) throws Exception {
                        httpRequestDialog = new HttpRequestDialog();
                        if (cancelable) {
                            httpRequestDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    disposable.dispose();
                                }
                            });
                        }
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (httpRequestDialog.isShowing()) {
                            httpRequestDialog.cancel();
                        }
                    }
                });
            }
        };
    }
}
