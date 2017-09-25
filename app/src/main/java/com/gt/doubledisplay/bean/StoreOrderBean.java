package com.gt.doubledisplay.bean;

import java.util.List;

/**
 * Created by wzb on 2017/9/13 0013.
 */

public class StoreOrderBean {

    /**
     * cashier :
     * consumption_money : 0.01
     * fansCurrency_deduction : 0
     * integral_deduction : 0
     * member_deduction : 0
     * menus : [{"commnt":"","menu_no":"1003","money":0.01,"name":"小白兔套餐","norms":"油炸  小份  热销","num":1}]
     * order_code : DD1505726826378
     * order_id : A00029
     * order_time : 2017-09-18 17:27:06
     * payWay : 现金支付
     * pay_money : 0.01
     * pay_time : 2017-09-18 17:27:08
     * pay_type : 1
     * print_type : 1
     * qrUrl : http://nb.canyin.deeptel.com.cn//simple/79B4DE7C/orderDetailtoxp.do?orderId=2723
     * remark :
     * result : 1
     * shop_adress : 广东省江门市江海区江海区滘头街道勤政路政府c栋513
     * shop_name : 谷通科技
     * shop_phone : 13528307867
     * yhq_deduction : 0
     */

    private String cashier;
    private double consumption_money;
    private double fansCurrency_deduction;
    private double integral_deduction;
    private double member_deduction;
    private String order_code;
    private String order_id;
    private String order_time;
    private String payWay;
    private double pay_money;
    private String pay_time;
    private int pay_type;
    private int print_type;
    private String qrUrl;
    private String remark;
    private int result;
    private String shop_adress;
    private String shop_name;
    private String shop_phone;
    private double yhq_deduction;
    private List<MenusBean> menus;

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

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

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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

    public double getPay_money() {
        return pay_money;
    }

    public void setPay_money(double pay_money) {
        this.pay_money = pay_money;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public int getPrint_type() {
        return print_type;
    }

    public void setPrint_type(int print_type) {
        this.print_type = print_type;
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getShop_adress() {
        return shop_adress;
    }

    public void setShop_adress(String shop_adress) {
        this.shop_adress = shop_adress;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public void setShop_phone(String shop_phone) {
        this.shop_phone = shop_phone;
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
         * commnt :
         * menu_no : 1003
         * money : 0.01
         * name : 小白兔套餐
         * norms : 油炸  小份  热销
         * num : 1
         */

        private String commnt;
        private String menu_no;
        private double money;
        private double original_price;
        private String name;
        private String norms;
        private int num;

        public double getOriginal_price() {
            return original_price;
        }

        public void setOriginal_price(double original_price) {
            this.original_price = original_price;
        }

        public String getCommnt() {
            return commnt;
        }

        public void setCommnt(String commnt) {
            this.commnt = commnt;
        }

        public String getMenu_no() {
            return menu_no;
        }

        public void setMenu_no(String menu_no) {
            this.menu_no = menu_no;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNorms() {
            return norms;
        }

        public void setNorms(String norms) {
            this.norms = norms;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
