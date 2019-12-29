package com.zidongxiangxi.reliablemq.producer.transaction;

import com.zidongxiangxi.reliabelmq.api.transaction.TransactionListener;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.Objects;

/**
 * 事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class DefaultTransactionSynchronization implements TransactionSynchronization {
    private TransactionListener transactionListener;

    public DefaultTransactionSynchronization(TransactionListener transactionListener) {
        this.transactionListener = transactionListener;
    }

    @Override
    public void suspend() {
        if (Objects.nonNull(transactionListener)) {
            transactionListener.suspend();
        }
    }

    @Override
    public void resume() {
        if (transactionListener != null) {
            transactionListener.resume();
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (readOnly || transactionListener == null) {
            return;
        }
        transactionListener.beforeCommit();
    }

    @Override
    public void beforeCompletion() {
        if (Objects.nonNull(transactionListener)) {
            return;
        }
        transactionListener.beforeCompletion();
    }

    @Override
    public void afterCommit() {
        if (Objects.nonNull(transactionListener)) {
            transactionListener.afterCommit();
        }
    }

    @Override
    public void afterCompletion(int status) {
        if (Objects.nonNull(transactionListener)) {
            transactionListener.afterCompletion();
        }
    }
}
