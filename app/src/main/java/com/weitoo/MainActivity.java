/*
package com.weitoo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gt.doubledisplay.R;
import com.up.BunflyMoneyBox;
import com.weitoo.printer.MsPrinter;
import com.weitoo.printer.OrderTicketModel;
import com.weitoo.printer.QRCodeModel;
import com.weitoo.printer.TicketPrinter;

import java.util.concurrent.TimeUnit;

*/
/**
 * Created by Awen on 2016/10/20.
 *//*

public class MainActivity extends Activity {

    private volatile boolean boxFlag;
    private volatile boolean printFlag;
    private volatile int curBoxTimes;
    private volatile int curPrintTimes;
    private volatile int boxSpace;
    private volatile int printSpace;
    private volatile int boxTimes;
    private volatile int printTimes;
    private volatile int totalBox;
    private volatile int totalPrint;

    WtHandler handler = new WtHandler();

    OrderTicketModel model;
    QRCodeModel qrCodeModel;
    BunflyMoneyBox moneyBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        init();
        findViewById(R.id.btn_start_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curPrintTimes = 0;
                printSpace = getEtValue(R.id.et_print_space);
                printTimes = getEtValue(R.id.et_print_times);
                printFlag = !printFlag;
                if (printFlag) {
                    setEtText(R.id.btn_start_print, "停止");
                    new Thread(printRun).start();
                } else {
                    setEtText(R.id.btn_start_print, "开始");
                }
            }
        });

        findViewById(R.id.btn_start_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curBoxTimes = 0;
                boxTimes = getEtValue(R.id.et_box_times);
                boxSpace = getEtValue(R.id.et_box_space);
                boxFlag = !boxFlag;
                if (boxFlag) {
                    setEtText(R.id.btn_start_box, "停止");
                    new Thread(boxRun).start();
                } else {
                    setEtText(R.id.btn_start_box, "开始");
                }
            }
        });

        findViewById(R.id.btMoneyBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsPrinter.openMoneyBox();
            }
        });

        findViewById(R.id.btMsPrinter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsPrinter.printTicket();
            }
        });

        findViewById(R.id.btMoneyBoxMsPrinter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsPrinter.printTicket();
                MsPrinter.openMoneyBox();
            }
        });
    }

    private void init() {
        // 打印小票
        model = new OrderTicketModel();
        model.packPrintData();

        qrCodeModel = new QRCodeModel();
        qrCodeModel.printQRCode();

        moneyBox = new BunflyMoneyBox();

    }

    private Runnable boxRun = new Runnable() {
        @Override
        public void run() {
            while (boxFlag) {
                try {
                    TimeUnit.SECONDS.sleep(boxSpace);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (curBoxTimes++ < boxTimes) {
                    moneyBox.boxopen();
                    totalBox++;
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        }
    };

    private Runnable printRun = new Runnable() {
        @Override
        public void run() {
            while (printFlag) {
                try {
                    TimeUnit.SECONDS.sleep(printSpace);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (curPrintTimes++ < printTimes) {
                    TicketPrinter.getInstance().print(model);
                    TicketPrinter.getInstance().print(qrCodeModel);

                    MsPrinter.printTicket();
                    totalPrint++;
                    handler.sendEmptyMessage(1);
                } else {
                    handler.sendEmptyMessage(3);
                }

            }
        }
    };

    private int getEtValue(int id) {
        String et = ((EditText)findViewById(id)).getText().toString();
        if (et == null || et.equals("")) {
            et = "0";
        }

        return Integer.parseInt(et);
    }

    private void setEtText(int id, String value) {
        ((TextView)findViewById(id)).setText(value);
    }

    class WtHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setEtText(R.id.tv_box_count, "总计："+totalBox);
                    break;
                case 1:
                    setEtText(R.id.tv_print_count, "总计："+totalPrint);
                    break;
                case 2:
                    boxFlag = false;
                    setEtText(R.id.btn_start_box, "开始");
                    break;
                case 3:
                    printFlag = false;
                    setEtText(R.id.btn_start_print, "开始");
                    break;
            }
        }
    }
}
*/
