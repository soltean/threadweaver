package com.so.junit.rules;

public class Item {

    private int reservePrice;
    private String code;

    public Item(String code, int reservePrice) {
        this.code = code;
        this.reservePrice = reservePrice;
    }

    public int getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(int reservePrice) {
        this.reservePrice = reservePrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
