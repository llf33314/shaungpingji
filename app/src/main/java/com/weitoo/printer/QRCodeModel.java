package com.weitoo.printer;

/**
 * Created by Awen on 2016/10/17.
 */
public class QRCodeModel extends AbstractTicketModel {

    public QRCodeModel() {
        super(false);
    }

    public void printQRCode() {
        appendQRCode(data);
        appendEndSpace(data);
    }

    void appendQRCode(StringBuilder data) {
        //二维码
        data.append(PRINTERCONFIG.CenterCMD);
        data.append(PRINTERCONFIG.Absolute);
        data.append(PRINTERCONFIG.QRCode);
    }

}
