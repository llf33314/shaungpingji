package com.gt.doubledisplay.bean;

import java.util.List;

/**
 * Created by wzb on 2017/9/18 0018.
 */

public class TakeOutOrderBean {


    /**
     * consumption_money : 0.01
     * fansCurrency_deduction : 0
     * integral_deduction : 0
     * member_deduction : 0
     * menAddress : 南山区科技园兰光科技园(北环大道北)  -无门牌
     * menName : 822
     * menPhone : 13168446337
     * menus : [{"det_food_name":"小白兔套餐","det_food_num":1,"det_food_price":0.01,"food_picture":"//image/3/sky123456/3/20170616/5FBE3AD1E208297829C07EC6531348B4.jpg","menu_no":"1003","payPrice":0.01}]
     * order_id : W00004
     * order_no : ED1504682701831
     * order_time : 1504682701000
     * payWay : 现金支付
     * print_time : 2017-09-18 19:42:37
     * qrUrl : http://nb.canyin.deeptel.com.cn//simple/79B4DE7C/orderDetailforWaiMai.do?orderId=49679
     * remark :
     * resAddress : 广东省江门市江海区江海区滘头街道勤政路政府c栋513
     * resName : 谷通科技
     * resPhone : 13528307867
     * totalNum : 1
     * yhq_deduction : 0
     */

    private double consumption_money;
    private double fansCurrency_deduction;
    private double integral_deduction;
    private double member_deduction;
    private String menAddress;
    private String menName;
    private String menPhone;
    private String order_id;
    private String order_no;
    private String order_time;
    private String payWay;
    private String print_time;
    private String qrUrl;
    private String remark;
    private String resAddress;
    private String resName;
    private String resPhone;
    private int totalNum;
    private double yhq_deduction;
    private List<MenusBean> menus;

    public double getConsumption_money() {
        return consumption_money;
    }

    public void setConsumption_money(double consumption_money) {
        this.consumption_money = consumption_money;
    }

    public double getFansCurrency_deduction() {
        return fansCurrency_deduction;
    }

    public void setFansCurrency_deduction(double fansCurrency_deduction) {
        this.fansCurrency_deduction = fansCurrency_deduction;
    }

    public double getIntegral_deduction() {
        return integral_deduction;
    }

    public void setIntegral_deduction(double integral_deduction) {
        this.integral_deduction = integral_deduction;
    }

    public double getMember_deduction() {
        return member_deduction;
    }

    public void setMember_deduction(double member_deduction) {
        this.member_deduction = member_deduction;
    }

    public String getMenAddress() {
        return menAddress;
    }

    public void setMenAddress(String menAddress) {
        this.menAddress = menAddress;
    }

    public String getMenName() {
        return menName;
    }

    public void setMenName(String menName) {
        this.menName = menName;
    }

    public String getMenPhone() {
        return menPhone;
    }

    public void setMenPhone(String menPhone) {
        this.menPhone = menPhone;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getPrint_time() {
        return print_time;
    }

    public void setPrint_time(String print_time) {
        this.print_time = print_time;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getResAddress() {
        return resAddress;
    }

    public void setResAddress(String resAddress) {
        this.resAddress = resAddress;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResPhone() {
        return resPhone;
    }

    public void setResPhone(String resPhone) {
        this.resPhone = resPhone;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public double getYhq_deduction() {
        return yhq_deduction;
    }

    public void setYhq_deduction(double yhq_deduction) {
        this.yhq_deduction = yhq_deduction;
    }

    public List<MenusBean> getMenus() {
        return menus;
    }

    public void setMenus(List<MenusBean> menus) {
        this.menus = menus;
    }

    public static class MenusBean {
        /**
         * det_food_name : 小白兔套餐
         * det_food_num : 1
         * det_food_price : 0.01
         * food_picture : //image/3/sky123456/3/20170616/5FBE3AD1E208297829C07EC6531348B4.jpg
         * menu_no : 1003
         * payPrice : 0.01
         */

        private String det_food_name;
        private int det_food_num;
        private double det_food_price;
        private String food_picture;
        private String menu_no;
        private double payPrice;
        private String remark="";
        private String norms="";

        public String getDet_food_name() {
            return det_food_name;
        }

        public void setDet_food_name(String det_food_name) {
            this.det_food_name = det_food_name;
        }

        public int getDet_food_num() {
            return det_food_num;
        }

        public void setDet_food_num(int det_food_num) {
            this.det_food_num = det_food_num;
        }

        public double getDet_food_price() {
            return det_food_price;
        }

        public void setDet_food_price(double det_food_price) {
            this.det_food_price = det_food_price;
        }

        public String getFood_picture() {
            return food_picture;
        }

        public void setFood_picture(String food_picture) {
            this.food_picture = food_picture;
        }

        public String getMenu_no() {
            return menu_no;
        }

        public void setMenu_no(String menu_no) {
            this.menu_no = menu_no;
        }

        public double getPayPrice() {
            return payPrice;
        }

        public void setPayPrice(double payPrice) {
            this.payPrice = payPrice;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getNorms() {
            return norms;
        }

        public void setNorms(String norms) {
            this.norms = norms;
        }
    }
}
