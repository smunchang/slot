package com.kaybo.slot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameData {

    private int spinCount;
    private List<History> items;

    public int getSpinCount() {
        return spinCount;
    }

    public void setSpinCount(int spinCount) {
        this.spinCount = spinCount;
    }

    public List<History> getItems() {
        return items;
    }

    public void setItems(List<History> items) {
        this.items = items;
    }
}
