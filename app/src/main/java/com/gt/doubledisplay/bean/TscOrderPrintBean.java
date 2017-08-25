package com.gt.doubledisplay.bean;

import java.util.List;

/**
 * Created by wzb on 2017/8/25 0025.
 */

public class TscOrderPrintBean {
    private int fansCurrency_deduction;
    private int member_deduction;
    private String order_time;
    private int print_type;
    private String shop_name;
    private double pay_money;
    private String pay_time;
    private int result;
    private String order_code;
    private String shop_adress;
    private String cashier;
    private int pay_type;
    private double consumption_money;
    private List<Menus> menus;
    private String shop_phone;
    private String integral_deduction;

    public int getFansCurrency_deduction() {
        return fansCurrency_deduction;
    }

    public void setFansCurrency_deduction(int fansCurrency_deduction) {
        this.fansCurrency_deduction = fansCurrency_deduction;
    }

    public int getMember_deduction() {
        return member_deduction;
    }

    public void setMember_deduction(int member_deduction) {
        this.member_deduction = member_deduction;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public int getPrint_type() {
        return print_type;
    }

    public void setPrint_type(int print_type) {
        this.print_type = print_type;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getShop_adress() {
        return shop_adress;
    }

    public void setShop_adress(String shop_adress) {
        this.shop_adress = shop_adress;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public double getConsumption_money() {
        return consumption_money;
    }

    public void setConsumption_money(double consumption_money) {
        this.consumption_money = consumption_money;
    }

    public List<Menus> getMenus() {
        return menus;
    }

    public void setMenus(List<Menus> menus) {
        this.menus = menus;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public void setShop_phone(String shop_phone) {
        this.shop_phone = shop_phone;
    }

    public String getIntegral_deduction() {
        return integral_deduction;
    }

    public void setIntegral_deduction(String integral_deduction) {
        this.integral_deduction = integral_deduction;
    }

    public static class Menus {
        private String menu_no;
        private double money;
        private String norms;
        private int num;
        private String name;
        private String commnt;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
