package com.kanyu.chat.controller;

import cn.hutool.json.JSONUtil;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.utils.UserHolder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class ChatBotController {
    //注入模型，配置文件中的模型，或者可以在方法中指定模型
    @Resource
    private OllamaChatModel model;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
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

    //缓存
    public Flux<ChatContent> findBySessionId(String sessionId) {
        String cacheKey = "chat:history:" + sessionId;

        // 1. 先查询 Redis 缓存
        List<String> cachedList = stringRedisTemplate.opsForList().range(cacheKey, 0, -1);
        if (cachedList != null && !cachedList.isEmpty()) {
            return Flux.fromStream(cachedList.stream()
                    .map(json -> JSONUtil.toBean(json, ChatContent.class)));
        }

        // 2. 缓存未命中则查数据库
        List<ChatContent> dbList = chatService.selectBySessionId(sessionId);


        //缓存穿透空值处理
        // 空结果缓存处理
        if (dbList.isEmpty()) {
            stringRedisTemplate.opsForValue().set(cacheKey, "", 5, TimeUnit.MINUTES);  // 空值缓存短时间:ml-citation{ref="6" data="citationList"}
        }

        // 3. 序列化并写入 Redis
        List<String> jsonList = dbList.stream()
                .map(JSONUtil::toJsonStr)
                .collect(Collectors.toList());

        stringRedisTemplate.opsForList().rightPushAll(cacheKey, jsonList);
        stringRedisTemplate.expire(cacheKey, 30, TimeUnit.MINUTES);  // 设置缓存过期时间:ml-citation{ref="6" data="citationList"}

        return Flux.fromIterable(dbList);
    }
    @GetMapping(value = "/streamChat", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam("message")String message){
        //创建随机会话 ID 存储用户发送对话
        Long userId = UserHolder.getUser().getId();
        ChatContent chatContent = new ChatContent();
        chatContent.setMessage(message);
        chatContent.setSendUserId(userId);
        String sessionId = "333";
        chatContent.setReceiveUserId(Long.valueOf(sessionId));
        //存储机器人的回答消息

        ChatClient chatClient = ChatClient.builder(this.model).defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10)).build();
        return chatClient.prompt(message)
                .stream()
                .content()
                .delayElements(Duration.ofMillis(50)) // 控制发送频率
                .flatMap(reply -> {
                    // 保存机器人回复
                    ChatContent botMsg = new ChatContent();
                    botMsg.setSendUserId(Long.valueOf(sessionId));
                    botMsg.setMessage(reply);
                    botMsg.setReceiveUserId(userId);
                    return Mono.fromCallable(() -> {
                        chatService.save(botMsg); // 非阻塞包装
                        return reply;
                    }).subscribeOn(Schedulers.boundedElastic()); // 异步执行:ml-citation{ref="6,8" data="citationList"}
                });
    }
}
