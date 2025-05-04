package com.kanyu.chat.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;

import java.util.UUID;
@RestController
public class ChatBotController {
    //注入模型，配置文件中的模型，或者可以在方法中指定模型
    @Resource
    private OllamaChatModel model;

    //模拟数据库存储会话和消息
    ChatMemory chatMemory = new InMemoryChatMemory();

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message){
        String call = model.call(message);
        System.out.println(call);
        return call;

    }

    @GetMapping(value = "/streamChat", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam("message")String message){
        //创建随机会话 ID
        String sessionId = UUID.randomUUID().toString();
        ChatClient chatClient = ChatClient.builder(this.model).defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10)).build();
        return chatClient.prompt(message).stream().content();
    }
}
