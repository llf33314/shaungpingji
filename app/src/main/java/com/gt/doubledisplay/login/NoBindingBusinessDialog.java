package com.gt.doubledisplay.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.widget.TextView;

import com.gt.doubledisplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/2 0002.
 * 陈丹erp登录接口没有绑定任何erp提示
 */

public class NoBindingBusinessDialog extends Dialog {
    @BindView(R.id.no_bingding_cancel)
    TextView noBingdingCancel;

    public NoBindingBusinessDialog(@NonNull Context context) {
        super(context);
    }

    public NoBindingBusinessDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_bingding_business);
        ButterKnife.bind(this);
        this.setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.no_bingding_cancel)
    public void onViewClicked() {
        this.dismiss();
    }
}
