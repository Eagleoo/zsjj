package com.yda.handWine.Bean;

public class OrderInfo {


    /**
     * status : 1
     * state : 0
     * orderno : R18060614423454
     * money : 1000.0
     */

    private int status;
    private int state;
    private String orderno;
    private int Pay_type;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private double money;

    public int getPay_type() {
        return Pay_type;
    }

    public void setPay_type(int pay_type) {
        Pay_type = pay_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    private modelBean model;

    public modelBean getModel() {
        return model;
    }

    public void setModel(modelBean model) {
        this.model = model;
    }

    public static class modelBean {

        private int state;
        private int userid;
        private String cell_name;
        private double order_amount;
        private String orderno;
        private String user_name;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getCell_name() {
            return cell_name;
        }

        public void setCell_name(String cell_name) {
            this.cell_name = cell_name;
        }

        public double getOrder_amount() {
            return order_amount;
        }

        public void setOrder_amount(double order_amount) {
            this.order_amount = order_amount;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
