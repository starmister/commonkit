package com.songwy.utils.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private StateMachine<OrderStatus,OrderEvents> stateMachine;

    public void run(String... strings) throws Exception {
        stateMachine.start();
        stateMachine.sendEvent(OrderEvents.PAY);
        stateMachine.sendEvent(OrderEvents.RECEIVE);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
