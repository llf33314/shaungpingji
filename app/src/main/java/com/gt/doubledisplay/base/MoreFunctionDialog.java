package com.gt.doubledisplay.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gt.doubledisplay.R;


/**
 * Created by wzb on 2017/7/24 0024.
 */

public class MoreFunctionDialog extends Dialog {

    private TextView tvCancel;

    private TextView btnOk;
    private TextView btnCancel;

    private String msg="";

    private Button confirmButton;
    private TextView hintMsg;
    public MoreFunctionDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public MoreFunctionDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }
    public MoreFunctionDialog(@NonNull Context context, String msg, @StyleRes int themeResId) {
        super(context, themeResId);
        this.msg=msg;
        init();

    }

    private void init(){
        setContentView(R.layout.dialog_more_function);
        tvCancel= (TextView) this.findViewById(R.id.tv_more_function_cancel);
        hintMsg= (TextView) this.findViewById(R.id.hint_msg);
        btnOk= (TextView) this.findViewById(R.id.dialog_more_ok);
        btnCancel= (TextView) this.findViewById(R.id.dialog_more_cancel);


        if (!TextUtils.isEmpty(msg)){
            hintMsg.setText(msg);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreFunctionDialog.this.dismiss();
            }
        });

    }

    public TextView getTvCancel() {
        return tvCancel;
    }

    /**
     * 弹出对话框并且显示下面俩个按钮
     */
    public void showButton(@Nullable View.OnClickListener okListener,@Nullable View.OnClickListener cancelListener){
            if (okListener!=null){
                btnOk.setVisibility(View.VISIBLE);
            }
            if (cancelListener!=null){
                btnCancel.setVisibility(View.VISIBLE);
            }
            btnOk.setOnClickListener(okListener);
            btnCancel.setOnClickListener(cancelListener);
           this.show();
    }

}
