package com.kaybo.slot;

public class AppException extends RuntimeException{


    private int errCode;
    public AppException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
