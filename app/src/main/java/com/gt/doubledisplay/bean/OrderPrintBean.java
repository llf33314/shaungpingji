package com.gt.doubledisplay.bean;

import java.util.List;

/**
 * Created by wzb on 2017/9/13 0013.
 */

public class OrderPrintBean {

    /**
     * cashier :
     * consumption_money : 4.32
     * fansCurrency_deduction : 1
     * integral_deduction : 1
     * member_deduction : 0
     * menus : [{"commnt":"","money":3.3,"name":"叉烧包","norms":"微辣  紫色","num":1},{"commnt":"","menu_no":"1003","money":1,"name":"黄金叉烧","norms":"微辣  小份  白色","num":1},{"commnt":"","menu_no":"3362","money":0.02,"name":"洋葱炒肉片","norms":"小份  中辣","num":2}]
     * order_code : DD1505292021472
     * order_id : A00001
     * order_time : 2017-09-13 16:40:21
     * pay_money : 4.32
     * pay_time : 2017-09-13 16:40:33
     * pay_type : 1
     * print_type : 1
     * result : 1
     * shop_adress : 广东省深圳市南山区兰光科技园C座513
     * shop_name : 谷通科技
     * shop_phone : 0755-26609632
     */

    private String cashier;
    private double consumption_money;
    private int fansCurrency_deduction;
    private int integral_deduction;
    private int member_deduction;
    private String order_code;
    private String order_id;
    private String order_time;
    private double pay_money;
    private String pay_time;
    private int pay_type;
    private int print_type;
    private int result;
    private String shop_adress;
    private String shop_name;
    private String shop_phone;
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

    public int getFansCurrency_deduction() {
        return fansCurrency_deduction;
    }

    public void setFansCurrency_deduction(int fansCurrency_deduction) {
        this.fansCurrency_deduction = fansCurrency_deduction;
    }

    public int getIntegral_deduction() {
        return integral_deduction;
    }

    public void setIntegral_deduction(int integral_deduction) {
        this.integral_deduction = integral_deduction;
    }

    public int getMember_deduction() {
        return member_deduction;
    }

    public void setMember_deduction(int member_deduction) {
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

    public List<MenusBean> getMenus() {
        return menus;
    }

    public void setMenus(List<MenusBean> menus) {
        this.menus = menus;
    }

    public static class MenusBean {
        /**
         * commnt :
         * money : 3.3
         * name : 叉烧包
         * norms : 微辣  紫色
         * num : 1
         * menu_no : 1003
         */

        private String commnt;
        private double money;
        private String name;
        private String norms;
        private int num;
        private String menu_no;

        public String getCommnt() {
            return commnt;
        }

        public void setCommnt(String commnt) {
            this.commnt = commnt;
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

        public String getMenu_no() {
            return menu_no;
        }

        public void setMenu_no(String menu_no) {
            this.menu_no = menu_no;
        }
    }
}
