package com.gt.doubledisplay.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class ChooseBusinessDialog extends Dialog {

    @BindView(R.id.rv_choose_business)
    RecyclerView rvChooseBusiness;
    @BindView(R.id.choose_business_cancel)
    TextView chooseBusinessCancel;

    //item 间距 dp
    private final int SPACE=40;


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
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvChooseBusiness.setLayoutManager(layoutManager);
        rvChooseBusiness.addItemDecoration(new ChooseBusinessAdapter.SpaceItemDecoration(ConvertUtils.dp2px(SPACE)));
        rvChooseBusiness.setAdapter(new ChooseBusinessAdapter(this.getContext(), getBusiness(),ConvertUtils.dp2px(SPACE)));
        this.setCanceledOnTouchOutside(false);

    }

    private List<BusinessBean> getBusiness() {
        List<BusinessBean> listBean = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            listBean.add(new BusinessBean());
        }
        return listBean;
    }

    @OnClick(R.id.choose_business_cancel)
    public void onViewClicked() {
        this.dismiss();
    }
}
