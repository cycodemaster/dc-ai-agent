package com.cy.dcaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * 自定义日志顾问（相当于拦截器）
 * 打印info级别日志，只输出单次用户提示词和AI回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
        // 使用MessageAggregator将流式响应聚合成单个AdvisedResponse
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponseFlux,this::observeAfter);
    }

    /**
     * 打印请求日志
     * @param advisedRequest 请求
     * @return 请求
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest){
        log.info("AI Request: {}",advisedRequest.userText());
        return advisedRequest;
    }

    /**
     * 打印响应日志
     * @param advisedResponse 响应
     */
    private void observeAfter(AdvisedResponse advisedResponse){
        log.info("AI Response: {}",advisedResponse.response().getResult().getOutput().getText());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 值越小优先级越高
        return 0;
    }
}
