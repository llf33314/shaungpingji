package com.gt.doubledisplay.update.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gt.doubledisplay.R;


/**
 */

public class UpdateDialog extends Dialog {


    private String contentStr="";
    private String titleStr="";

    private LinearLayout btnsLl;

    private Button confirmButton;

    private Button cancelButton;


    private TextView content;
    private TextView title;


    private ProgressBar progressBar;
    public UpdateDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public UpdateDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }
    public UpdateDialog(@NonNull Context context,String title,String content, @StyleRes int themeResId) {
        super(context, themeResId);
        this.contentStr=content;
        this.titleStr=title;
        init();

    }

    private void init(){
        setContentView(R.layout.dialog_ask_update);
        cancelButton= (Button) this.findViewById(R.id.cancel);
        btnsLl=(LinearLayout)this.findViewById(R.id.update_btns_ll);
        content= (TextView) this.findViewById(R.id.dialog_content);
        title= (TextView) this.findViewById(R.id.dialog_title);
        progressBar=(ProgressBar)this.findViewById(R.id.bar);
        if (!TextUtils.isEmpty(titleStr)){
            title.setText(titleStr);
        }
        if (!TextUtils.isEmpty(contentStr)){
            content.setText(contentStr);
        }
        confirmButton=(Button)this.findViewById(R.id.confirm);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDialog.this.dismiss();
            }
        });

    }

    public Button getConfirmButton() {
        return confirmButton;
    }
    public Button getCancelButton() {
        return cancelButton;
    }

    public void goneBtns(){
        btnsLl.setVisibility(View.GONE);
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public TextView getContent() {
        return content;
    }

}
