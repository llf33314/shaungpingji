package com.gt.doubledisplay.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseRecyclerAdapter;
import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.bean.DeviceBean;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.utils.RxBus;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class ChooseBusinessDialog extends Dialog {

    public static final String REMEMBER_BUSINESS_CHOOSE="remember_business_choose";

    @BindView(R.id.rv_choose_business)
    RecyclerView rvChooseBusiness;
    @BindView(R.id.choose_business_cancel)
    TextView chooseBusinessCancel;
    @BindView(R.id.login_choose_bus_cb)
    CheckBox chooseCb;

    List<LoginBean.ErplistBean> erpList;


    //item 间距 dp
    private final int SPACE=40;

    private ChooseBusinessAdapter mChooseBusinessAdapter;

    private Context mContext;


    public ChooseBusinessDialog(@NonNull Context context) {
        super(context);
    }

    public ChooseBusinessDialog(@NonNull Context context, @StyleRes int themeResId,List<LoginBean.ErplistBean> erpList) {
        super(context, themeResId);
        this.erpList=erpList;
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_business);
        this.setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvChooseBusiness.setLayoutManager(layoutManager);
        rvChooseBusiness.addItemDecoration(new ChooseBusinessAdapter.SpaceItemDecoration(ConvertUtils.dp2px(SPACE)));
        mChooseBusinessAdapter =new ChooseBusinessAdapter(this.getContext(), erpList,ConvertUtils.dp2px(SPACE));

        mChooseBusinessAdapter.setmOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, Object item, int position) {
                try {
                    LoginBean.ErplistBean erplistBean= (LoginBean.ErplistBean) item;
                    String  erpUrl=erplistBean.getItem_remark().split(",")[0];

                    Intent intent=new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(GTWebViewFrameLayout.PARAM_URL, erpUrl);
                    //ToastUtil.getInstance().showNewShort("登录成功");
                    mContext.startActivity(intent);

                    if (chooseCb.isChecked()){
                        Hawk.put(REMEMBER_BUSINESS_CHOOSE,erplistBean.getItem_key());
                    }

                    //显示副屏广告
                    RxBus.get().post(new DeviceBean(MyApplication.getLoginBean().getEqCode()));
                    if (mContext instanceof Activity){
                        if (ChooseBusinessDialog.this.isShowing()){
                            ChooseBusinessDialog.this.dismiss();
                        }
                        ((Activity)mContext).finish();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        rvChooseBusiness.setAdapter(mChooseBusinessAdapter);

    }

    @OnClick(R.id.choose_business_cancel)
    public void onViewClicked() {
        this.dismiss();
    }
}
