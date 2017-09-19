package com.gt.doubledisplay.printer.policy;

import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;

/**
 * Created by wzb on 2017/9/19 0019.
 */

public interface PrinterPolicy {
    /**
     * 打印到店订单
     */
    void printStoreOrder(StoreOrderBean storeOrderBean);

    /**
     * 打印外卖订单
     */
    void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean);
}
