package com.gt.doubledisplay.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.login.LoginActivity;
import com.gt.doubledisplay.printer.extraposition.bluetooth.BluetoothSettingActivity;
import com.gt.doubledisplay.setting.SettingActivity;
import com.gt.doubledisplay.utils.commonutil.AppManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;


/**
 * Created by wzb on 2017/7/14 0014.
 */

public  class BaseActivity extends RxAppCompatActivity {
    private RelativeLayout mToolbar;
    private TextView toolBarTitle;
    private Button btnSettting;

    //用于web查看当前地址
    private int titleClickCount=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // BarUtils.setStatusBarColor(this,getResources().getColor(R.color.toolbarBg));
        super.setContentView(R.layout.toolbar);
        init();
    }

    private void init(){
        mToolbar= (RelativeLayout) findViewById(R.id.base_toolbar);
        toolBarTitle= (TextView) findViewById(R.id.toolbar_title);
        btnSettting= (Button) findViewById(R.id.toolbar_blu);
        AppManager.getInstance().addActivity(this);
        btnSettting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BaseActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        toolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleClickCount++;
                if (titleClickCount%10==0){
                    titleTenClick();
                }

            }
        });
    }

    /**
     * 点击10次标题
     */
    protected void titleTenClick(){

    }

    public void showBtnBlu(){
        btnSettting.setVisibility(View.VISIBLE);
    }

    @CallSuper
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View activityView= LayoutInflater.from(this).inflate(layoutResID,null,false);
        ViewGroup viewGroup= (ViewGroup) mToolbar.getParent();
        viewGroup.addView(activityView);
        //空出边距给toolbar
        FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) activityView.getLayoutParams();
        lp.setMargins(0,(int)this.getResources().getDimension(R.dimen.toolbar_height),0,0);
        ButterKnife.bind(this);
    }

    @CallSuper
    @Override
    public void setContentView(View view) {
        ViewGroup viewGroup= (ViewGroup) mToolbar.getParent();
        viewGroup.addView(view);
        FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.setMargins(0,(int)this.getResources().getDimension(R.dimen.toolbar_height),0,0);
        ButterKnife.bind(this);
    }
    public void setToolBarTitle(String title){
        toolBarTitle.setText(title);
    }
    public void goneToolBar(){
        mToolbar.setVisibility(View.GONE);
    }

}
