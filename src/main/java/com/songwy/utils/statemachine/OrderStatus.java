package com.songwy.utils.statemachine;

public enum OrderStatus {

    CREATED,             //订单创建
    WAITING_FOR_RECEIVE, //待收货
    FINISHED             //订单完结
}
