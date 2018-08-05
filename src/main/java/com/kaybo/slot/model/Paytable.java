package com.kaybo.slot.model;

public class Paytable {
    private int id;
    private int symbolId;
    private int symbolGroup;
    private int method;
    private int value;
    private int multiplier;
    private int countIn10000;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(int symbolId) {
        this.symbolId = symbolId;
    }

    public int getSymbolGroup() {
        return symbolGroup;
    }

    public void setSymbolGroup(int symbolGroup) {
        this.symbolGroup = symbolGroup;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getCountIn10000() {
        return countIn10000;
    }

    public void setCountIn10000(int countIn10000) {
        this.countIn10000 = countIn10000;
    }

    @Override
    public String toString() {
        return "Paytable{" +
                "id=" + id +
                ", symbolId=" + symbolId +
                ", symbolGroup=" + symbolGroup +
                ", method=" + method +
                ", value=" + value +
                ", multiplier=" + multiplier +
                ", countIn10000=" + countIn10000 +
                '}';
    }
}
