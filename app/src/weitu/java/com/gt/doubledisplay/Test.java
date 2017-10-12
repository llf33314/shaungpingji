package com.gt.doubledisplay;

import com.gt.doubledisplay.base.MyApplication;
import com.gt.doubledisplay.utils.commonutil.ToastUtil;

/**
 * Created by wzb on 2017/10/10 0010.
 */

public class Test {

    public static final String appName="weitu";

    public static void Toast(){
        ToastUtil.getInstance().showToast("Toast",5000);
    }
}
