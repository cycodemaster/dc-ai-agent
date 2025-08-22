package com.cy.dcaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * springAI框架调用大模型
 */
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashScopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashScopeChatModel.call(new Prompt("你好，我是大陈")).getResult().getOutput();
        System.out.println(output.getText());
    }
}
