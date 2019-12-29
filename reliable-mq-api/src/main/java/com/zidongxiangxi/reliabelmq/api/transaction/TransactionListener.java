package com.zidongxiangxi.reliabelmq.api.transaction;

/**
 * 事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface TransactionListener {
    /**
     * 事务提交之前
     */
    void beforeCommit();

    /**
     * 事务提交或回滚之前，在beforeCommit之后
     */
    void beforeCompletion();

    /**
     * 事务提交之后
     */
    void afterCommit();

    /**
     * 事务提交或回滚之后，在afterCommit之后
     */
    void afterCompletion();

    /**
     * 事务挂起
     */
    void suspend();

    /**
     * 事务恢复
     */
    void resume();
}
