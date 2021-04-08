package com.songwy.utils.rocketmq.transcation;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;
import java.util.Random;

/**
 * @Date: Created in  2018/2/12 15:55
执行本地事务
 */
public class TransactionExecuterimpl implements LocalTransactionExecuter {
    @Override
    public LocalTransactionState executeLocalTransactionBranch(final Message message, final Object o) {
        try{
            //DB操作 应该带上事务 service -> dao
            //如果数据操作失败  需要回滚    同事返回RocketMQ一个失败消息  意味着 消费者无法消费到这条失败的消息
            //如果成功 就要返回一个rocketMQ成功的消息，意味着消费者将读取到这条消息
            //o就是attachment

            //测试代码
            if(new Random().nextInt(3) == 2){
                int a = 1 / 0;
            }
            System.out.println(new Date()+"===> 本地事务执行成功，发送确认消息");
        }catch (Exception e){
            System.out.println(new Date()+"===> 本地事务执行失败！！！");
            return LocalTransactionState.ROLLBACK_MESSAGE;

        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
