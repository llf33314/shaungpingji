package com.gt.doubledisplay.printer.policy;

import com.gt.doubledisplay.bean.StoreOrderBean;
import com.gt.doubledisplay.bean.TakeOutOrderBean;
import com.weitoo.printer.MsPrinter;

/**
 * Created by wzb on 2017/9/19 0019.
 */

public class WeituPrinter implements PrinterPolicy {
    @Override
    public void printStoreOrder(StoreOrderBean storeOrderBean) {
        MsPrinter.printStoreOrder(storeOrderBean);
    }

    @Override
    public void printTakeOutOrder(TakeOutOrderBean takeOutOrderBean) {
        MsPrinter.printTakeOutOrder(takeOutOrderBean);
    }
}
