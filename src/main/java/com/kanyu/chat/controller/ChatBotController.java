package com.kanyu.chat.controller;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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
    // Hutool雪花算法生成器
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake();

    // Redis键定义
    private static final String SESSION_PREFIX = "chat:session:";
    private static final String CONTEXT_PREFIX = "chat:context:";
    private static final int MAX_CONTEXT_LENGTH = 10; // 保留最近10轮对话

    /**
     * 生成唯一会话ID（Hutool雪花算法）
     */
    private String generateSessionId() {
        return "SESSION_" + SNOWFLAKE.nextIdStr();
    }
    /**
     * 创建新会话
     */
    @PostMapping("/new")
    public Map<String, Object> createNewSession() {
        String sessionId = generateSessionId();
        Long userId = UserHolder.getUser().getId();

        // 存储会话元数据
        Map<String, String> sessionMeta = new HashMap<>();
        sessionMeta.put("userId", userId.toString());
        sessionMeta.put("createTime", String.valueOf(System.currentTimeMillis()));
        stringRedisTemplate.opsForHash().putAll(SESSION_PREFIX + sessionId, sessionMeta);

        // 设置会话过期时间（2小时）
        stringRedisTemplate.expire(SESSION_PREFIX + sessionId, 2, TimeUnit.HOURS);

        return Map.of(
                "sessionId", sessionId,
                "expireAt", System.currentTimeMillis() + 7200_000
        );
    }

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
    public Flux<String> streamChat(@RequestParam("message")String message,@RequestParam String sessionId){
        Long userId = UserHolder.getUser().getId();

        return Flux.concat(
                // 阶段1：思考过程
                processThinking(message, sessionId, userId),

                // 阶段2：正式回答
                processAnswering(message, sessionId, userId)
        );}

    /**
     * 处理思考阶段（优化标识位置）
     */
    private Flux<String> processThinking(String message, String sessionId, Long userId) {
        return buildPromptWithContext(sessionId, message)
                .flatMapMany(prompt ->
                        // 添加首条消息标识
                        Flux.just("[THINKING] ")
                                .concatWith(model.stream(prompt).map(s -> s.replace("[THINKING] ", "")))
                )
                .windowUntilChanged(str -> str.endsWith(" ") || str.endsWith("\n")) // 按自然段落分组
                .flatMap(flux -> flux.reduce(String::concat))
                .doOnNext(content -> saveThinking(sessionId, userId, content))
                .delayElements(Duration.ofMillis(50));
    }

    /**
     * 处理回答阶段
     */
    private Flux<String> processAnswering(String message, String sessionId, Long userId) {
        return buildPromptWithContext(sessionId, message)
                .flatMapMany(prompt ->
                        model.stream(prompt)
                                .index()
                                .map(tuple -> {
                                    // 第一个消息添加标识
                                    if (tuple.getT1() == 0L) {
                                        return "[ANSWER] " + tuple.getT2();
                                    }
                                    return tuple.getT2();
                                })
                )
                .doOnNext(content -> saveMessage(sessionId, userId, message, content))
                .delayElements(Duration.ofMillis(30));
    }

    // 新增工具方法
    private Flux<String> wrapFirstElement(Flux<String> flux, String prefix) {
        return flux.index()
                .map(tuple -> tuple.getT1() == 0L ? prefix + tuple.getT2() : tuple.getT2());
    }
    /**
     * 构建带上下文的提示词（Hutool JSON处理）
     */
    private Mono<String> buildPromptWithContext(String sessionId, String currentMessage) {
        return Mono.fromCallable(() -> {
            // 从Redis获取对话上下文
            List<String> context = stringRedisTemplate.opsForList()
                    .range(CONTEXT_PREFIX + sessionId, 0, -1);


            // 使用Hutool构建JSON格式上下文
            String formattedContext = Optional.ofNullable(context)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(msg -> StrUtil.format("\"{}\"", msg))
                    .collect(Collectors.joining(","));
            log.info("上下文");
            log.info(formattedContext);

            // 构建完整提示词模板
            return StrUtil.format(
                    "你是一位中国人，用中文回答问题，基于以下对话历史（按时间倒序）：\n[{}]\n\n请回答最新问题：{}",
                    formattedContext, currentMessage
            );
        }).subscribeOn(Schedulers.boundedElastic());
    }


    /**
     * 保存消息到Redis和数据库（带事务）
     */
    @Transactional
    protected void saveMessage(String sessionId, Long userId, String question, String answer) {
//        // 保存用户问题
//        ChatContent userMsg = new ChatContent();
//        userMsg.setSessionId(sessionId);
//        userMsg.setMessage(question);
//        chatService.save(userMsg);

        // 保存AI回答
        ChatContent aiMsg = new ChatContent();
//        aiMsg.setSessionId(sessionId);
        aiMsg.setReceiveUserId(userId);
        aiMsg.setSendUserId(Long.valueOf(sessionId));
        aiMsg.setMessage(answer);
//        aiMsg.setType("ASSISTANT");
        chatService.save(aiMsg);

        // 更新Redis上下文
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.lPush((CONTEXT_PREFIX + sessionId).getBytes(),
                    question.getBytes(),
                    answer.getBytes()
            );
            connection.lTrim((CONTEXT_PREFIX + sessionId).getBytes(), 0, MAX_CONTEXT_LENGTH * 2 - 1);
            return null;
        });
    }

    /**
     * 保存思考过程到Redis（临时存储）
     */
    private void saveThinking(String sessionId, Long userId, String content) {
        String key = "chat:thinking:" + sessionId;
        stringRedisTemplate.opsForList().rightPush(key, content);
        stringRedisTemplate.expire(key, 10, TimeUnit.MINUTES);
    }
}
