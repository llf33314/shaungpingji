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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;


/**
 * Created by wzb on 2017/7/14 0014.
 */

public  class BaseActivity extends RxAppCompatActivity {
    private Toolbar mToolbar;
    private TextView toolBarTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // BarUtils.setStatusBarColor(this,getResources().getColor(R.color.toolbarBg));
        super.setContentView(R.layout.toolbar);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        toolBarTitle= (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
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
