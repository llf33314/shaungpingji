package com.gt.doubledisplay.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.gt.doubledisplay.R;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class ChooseBusinessDialog extends Dialog {
    public ChooseBusinessDialog(@NonNull Context context) {
        super(context);
    }

    public ChooseBusinessDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_business);
    }
}
