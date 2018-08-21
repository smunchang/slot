package com.kaybo.slot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Pool {
    @JsonIgnore
    private int id;
    private String symbols;
    @JsonIgnore
    private int paytableId;
    private int multiplier;
    private Reward reward;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public int getPaytableId() {
        return paytableId;
    }

    public void setPaytableId(int paytableId) {
        this.paytableId = paytableId;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
}
