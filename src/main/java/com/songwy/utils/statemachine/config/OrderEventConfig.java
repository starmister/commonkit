package com.songwy.utils.statemachine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
public class OrderEventConfig {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @OnTransition(target = "CREATED")
    public void create() {
        logger.info("订单创建，待支付");
    }

    @OnTransition(source = "CREATED", target = "WAITING_FOR_RECEIVE")
    public void pay() {
        logger.info("用户完成支付，待收货");
    }

    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "FINISHED")
    public void receive() {
        logger.info("用户已收货，订单完成");
    }
}
