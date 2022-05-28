package com.example.currencyexchange.data.model.constant;

public enum ErrorCodes {
    CURRENCY_FORMAT_ERROR(1),
    CURRENCY_NOT_FOUND(2),
    EXTERNAL_SERVICE_ERROR(3);

    private final int code;

    private ErrorCodes(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

}
