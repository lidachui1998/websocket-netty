package com.lidachui.websocket.service.policy;

import com.lidachui.websocket.dal.model.BroadcastMessage;


/**
 * AbstractMessageBroadcastPolicy
 *
 * @Author lihuijie
 * @Description: 广播消息策略
 * @SINCE 2023/4/17 23:19
 */
public abstract class AbstractMessageBroadcastPolicy {

    /**
     * 前置方法，子类可以选择性地覆盖该方法
     */
    protected void preProcess() {
        // 默认实现为空
    }

    /**
     * 广播消息 子类必须实现
     */
    public final void broadcastMessage(BroadcastMessage broadcastMessage) {
        // 执行前置处理逻辑
        preProcess();
        //读取配置
        // 实现广播消息逻辑
        doBroadcast(broadcastMessage);
        // 执行后置处理逻辑
        postProcess();
    }


    /**
     * 执行具体的广播消息逻辑，子类必须实现
     *
     * @param broadcastMessage 广播消息
     */
    protected abstract void doBroadcast(BroadcastMessage broadcastMessage);

    /**
     * 后置方法，子类可以选择性地覆盖该方法
     */
    protected void postProcess() {
        // 默认实现为空
    }
}
