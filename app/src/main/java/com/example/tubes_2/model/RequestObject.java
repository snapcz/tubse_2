package com.example.tubes_2.model;

public class RequestObject {
    String api_key;
    int order, value;

    public RequestObject(String api_key, int order, int value) {
        this.api_key = api_key;
        this.order = order;
        this.value = value;
    }
}
