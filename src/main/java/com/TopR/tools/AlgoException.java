package com.TopR.tools;

public class AlgoException extends RuntimeException{
    private ErrorCodeEnum errorCode;

    private String extraMsg="";

    public AlgoException(ErrorCodeEnum error) {

    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    @Override
    public String toString() {
        return "AlgoException{" +
                "errorCode=" + errorCode +
                ", extraMsg='" + extraMsg + '\'' +
                '}';
    }
}
