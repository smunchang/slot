package com.kaybo.slot.model;

public class Slot {
    private int id;
    private String name;
    private String size;
    private String betAmounts;
    private String symbolIds;
    private String paylineIds;
    private String patableIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBetAmounts() {
        return betAmounts;
    }

    public void setBetAmounts(String betAmounts) {
        this.betAmounts = betAmounts;
    }

    public String getSymbolIds() {
        return symbolIds;
    }

    public void setSymbolIds(String symbolIds) {
        this.symbolIds = symbolIds;
    }

    public String getPaylineIds() {
        return paylineIds;
    }

    public void setPaylineIds(String paylineIds) {
        this.paylineIds = paylineIds;
    }

    public String getPatableIds() {
        return patableIds;
    }

    public void setPatableIds(String patableIds) {
        this.patableIds = patableIds;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", betAmounts='" + betAmounts + '\'' +
                ", symbolIds='" + symbolIds + '\'' +
                ", paylineIds='" + paylineIds + '\'' +
                ", patableIds='" + patableIds + '\'' +
                '}';
    }
}
