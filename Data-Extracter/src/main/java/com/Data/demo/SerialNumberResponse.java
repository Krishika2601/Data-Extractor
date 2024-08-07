package com.Data.demo;



import java.util.List;

public class SerialNumberResponse {
    private String message;
    private int httpCode;
    private List<String> data;

    public SerialNumberResponse(String message, int httpCode, List<String> data) {
        this.message = message;
        this.httpCode = httpCode;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
