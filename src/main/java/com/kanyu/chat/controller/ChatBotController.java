package com.kanyu.chat.controller;

import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.utils.UserHolder;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
@RestController
public class ChatBotController {
    //注入模型，配置文件中的模型，或者可以在方法中指定模型
    @Resource
    private OllamaChatModel model;

    //引入存储消息服务
    @Resource
    private ChatService chatService;
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
        //创建随机会话 ID 存储用户发送对话
        ChatContent chatContent = new ChatContent();
        chatContent.setMessage(message);
        chatContent.setSendUserId(UserHolder.getUser().getId());
        String sessionId = UUID.randomUUID().toString();
        chatContent.setReceiveUserId(Long.valueOf(sessionId));
        //存储机器人的回答消息

        ChatClient chatClient = ChatClient.builder(this.model).defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10)).build();
        return chatClient.prompt(message)
                .stream()
                .content()
                .flatMap(reply -> {
                    // 保存机器人回复
                    ChatContent botMsg = new ChatContent();
                    botMsg.setSendUserId(Long.valueOf(sessionId));
                    botMsg.setMessage(reply);
                    return Mono.fromCallable(() -> {
                        chatService.save(botMsg); // 非阻塞包装
                        return reply;
                    }).subscribeOn(Schedulers.boundedElastic()); // 异步执行:ml-citation{ref="6,8" data="citationList"}
                });
    }
}
