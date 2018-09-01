package com.kaybo.slot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class History {

    private int id;
    @JsonIgnore
    private String userNo;
    private long achievedAt = -1;
    private long paidAt = -1;

    @JsonIgnore
    private Date achievedDate;
    @JsonIgnore
    private Date paidDate;

    @JsonIgnore
    private int condition;
    @JsonIgnore
    private int type;
    @JsonIgnore
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(long achievedAt) {
        this.achievedAt = achievedAt;
    }

    public long getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(long paidAt) {
        this.paidAt = paidAt;
    }

    public Date getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(Date achievedDate) {
        this.achievedDate = achievedDate;
        this.achievedAt = achievedDate.getTime();
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
        this.paidAt = paidDate.getTime();
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", userNo='" + userNo + '\'' +
                ", achievedAt=" + achievedAt +
                ", paidAt=" + paidAt +
                ", achievedDate=" + achievedDate +
                ", paidDate=" + paidDate +
                ", condition=" + condition +
                ", type=" + type +
                ", count=" + count +
                '}';
    }
}
