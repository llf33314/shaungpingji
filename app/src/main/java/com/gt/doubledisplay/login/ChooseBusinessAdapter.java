package com.gt.doubledisplay.login;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.gt.doubledisplay.base.BaseRecyclerAdapter;
import com.gt.doubledisplay.base.BaseViewHolder;
import com.gt.doubledisplay.bean.LoginBean;
import com.gt.doubledisplay.utils.commonutil.ConvertUtils;

import java.util.List;

/**
 * Created by wzb on 2017/8/4 0004.
 */

public class ChooseBusinessAdapter extends BaseRecyclerAdapter<LoginBean.ErplistBean>{

    private int space;

    public ChooseBusinessAdapter(Context context, List<LoginBean.ErplistBean> listBean,int space) {
        super(context, listBean);
        this.space=space;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_choose_business;
    }

    /**
     * rv都是包含内容 图片大小需要 正方形且页面需要显示三个item;
     */
    @Override
    protected void changeLayout(ViewGroup parent, View view) {

        //这里没办法动态获取，只能根据布局文件或者屏幕去获取
        int maxWidth= ConvertUtils.dp2px(600-25-25-parent.getPaddingBottom());
        int ivWidth=(maxWidth-space*2)/3;

        ImageView iv= (ImageView) view.findViewById(R.id.iv_item_choose_business);
        FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) iv.getLayoutParams();
        lp.width=ivWidth;
        lp.height=ivWidth;
        iv.setLayoutParams(lp);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, LoginBean.ErplistBean bean, int position) {
        try {//防止后台配置写错导致前端出错
            String imgUrl=bean.getItem_remark().split(",")[1];
            holder.setImageForUrl(R.id.iv_item_choose_business,imgUrl);
        }catch (Exception e){
            e.printStackTrace();
            holder.setImageResource(R.id.iv_item_choose_business,R.mipmap.ic_launcher);
        }

    }

    /**
     * 设置间距
     */
  public static  class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;
      public SpaceItemDecoration(int space) {
          this.mSpace = space;
      }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) !=state.getItemCount()-1) {
                outRect.right = mSpace;
            }
        }
        public int getSpace(){
            return this.mSpace;
        }
    }

}
