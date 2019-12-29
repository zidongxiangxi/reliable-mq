package com.zidongxiangxi.reliablemq.producer.transaction.listener;

import com.zidongxiangxi.reliabelmq.api.transaction.TransactionListener;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitProducerTransactionMessageHolder;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitTransactionContext;

import java.util.Stack;

/**
 * 抽象的rabbit producer事务监听
 *
 * @author chenxudong
 * @date 2019/09/18
 */
public abstract class AbstractRabbitProducerTransactionListener implements TransactionListener {
    private final ThreadLocal<Stack<RabbitProducerTransactionMessageHolder>> resource =
        ThreadLocal.withInitial(Stack::new);

    @Override
    public void suspend() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (messageHolder == null) {
            return;
        }
        RabbitTransactionContext.removeMessageHolder();
        resource.get().push(messageHolder);
    }

    @Override
    public void resume() {
        RabbitTransactionContext.setMessageHolder(resource.get().pop());
    }

    protected RabbitProducerTransactionMessageHolder remove() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        RabbitTransactionContext.removeMessageHolder();
        return messageHolder;
    }
}
