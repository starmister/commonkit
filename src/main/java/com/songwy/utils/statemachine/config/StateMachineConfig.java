package com.songwy.utils.statemachine.config;

import com.songwy.utils.statemachine.OrderEvents;
import com.songwy.utils.statemachine.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.EnumSet;
@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderEvents> {

    private Logger logger = LoggerFactory.getLogger(StateMachineConfig.class);

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvents> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.CREATED)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.WAITING_FOR_RECEIVE)
                .event(OrderEvents.PAY)
                .and()
                .withExternal()
                .source(OrderStatus.WAITING_FOR_RECEIVE).target(OrderStatus.FINISHED)
                .event(OrderEvents.RECEIVE);
    }

//    @Override
//    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvents> config)
//            throws Exception {
//        // 指定状态机的处理监听器
//        config.withConfiguration().listener(listener());
//    }
//
//    @Bean
//    public StateMachineListener<OrderStatus, OrderEvents> listener() {
//        return new StateMachineListenerAdapter<OrderStatus, OrderEvents>() {
//
//            @Override
//            public void transition(Transition<OrderStatus, OrderEvents> transition) {
//                if(transition.getTarget().getId() == OrderStatus.CREATED) {
//                    logger.info("订单创建，待支付");
//                    return;
//                }
//                if(transition.getSource().getId() == OrderStatus.CREATED
//                        && transition.getTarget().getId() == OrderStatus.WAITING_FOR_RECEIVE) {
//                    logger.info("用户完成支付，待收货");
//                    return;
//                }
//
//                if(transition.getSource().getId() == OrderStatus.WAITING_FOR_RECEIVE
//                        && transition.getTarget().getId() == OrderStatus.FINISHED) {
//                    logger.info("用户已收货，订单完成");
//                    return;
//                }
//            }
//
//        };
//    }
}
