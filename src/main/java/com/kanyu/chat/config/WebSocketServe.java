package com.kanyu.chat.config;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kanyu.chat.entity.GroupMember;
import com.kanyu.chat.service.GroupMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author websocket服务
 */
@ServerEndpoint(value = "/imserver/{userId}")
@Component
public class WebSocketServe {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServe.class);

    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    private static GroupMemberService groupMemberService;

    // 通过静态方法注入
    public static void setGroupMemberService(GroupMemberService service) {
        groupMemberService = service;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessionMap.put(userId, session);
        log.info("新用户userId={}加入, 当前在线人数为：{}", userId, sessionMap.size());
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("users", array);
        for (Object key : sessionMap.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("userId", key);
            array.add(jsonObject);
        }
        sendAllMessage(JSONUtil.toJsonStr(result));
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessionMap.remove(userId);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", userId, sessionMap.size());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        log.info("服务端收到用户userId={}的消息:{}", userId, message);
        JSONObject obj = JSONUtil.parseObj(message);
        //前端发送过来的消息类型
        String chatType = obj.getStr("chatType");
        // 消息类型路由
        if ("group".equals(chatType)){
            handleGroupMessage(obj, userId);
        } else {
            String receiveUser = obj.getStr("receiveUserId");
            String text = obj.getStr("content");
//            handlePrivateMessage(msgObj, userId); // 原有单聊处理
             Session toSession = sessionMap.get(receiveUser);
            if (toSession != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("sendUserId", userId);
                jsonObject.set("receiveUserId", receiveUser);
                jsonObject.set("content", text);
                jsonObject.set("type",2); //type=2表示用户发送的消息
                this.sendMessage(jsonObject.toString(), toSession);
                log.info("发送给用户userId={}，消息：{}", receiveUser, jsonObject.toString());
            } else {
                log.info("发送失败，未找到用户username={}的session", receiveUser);
            }
        }

    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }


    /*
     * 向在线的用户发送指定消息
     * 若用户不在线，则不发送不报错，当用户在线的时候前端主动请求申请列表 type=1 好友请求消息
     *
    */
    public static void sendMessageOnline(Long userId, String message) {
        log.info("进入了发送好友消息函数");
        Session session = sessionMap.get(userId+"");
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
            } catch (Exception e) {
                log.error("消息发送失败: {}", e.getMessage());
            }
        }else {
            log.info("发送失败，未找到用户username={}的session", userId);
        }
    }


    /*
     * 向在线的用户发送处理消息同意和拒绝消息
     * 若用户不在线，则不发送不报错，当用户在线的时候前端主动请求好友同意和拒绝消息
     * type=3 系统消息
     */
    public static void sendMessageApply(Long userId, String message) {
        log.info("进入了发送好友处理消息函数");
        Session session = sessionMap.get(userId+"");
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
            } catch (Exception e) {
                log.error("消息发送失败: {}", e.getMessage());
            }
        }else {
            log.info("发送失败，未找到用户username={}的session", userId);
        }
    }

    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
    /*
    * 群聊消息处理
    * */

    private void handleGroupMessage(JSONObject msgObj, String senderId) {
        String groupId = msgObj.getStr("groupId");
        String content = msgObj.getStr("content");

//        // 1.保存群消息
//        GroupMessage message = saveGroupMessage(groupId, Long.parseLong(senderId), content);
        log.info("setGroupMemberService"+groupMemberService);
        // 2.获取群成员
        List<GroupMember> groupMemberList = groupMemberService.query().eq("group_id", groupId).list();

        // 3.广播消息
        broadcastGroupMessage(msgObj, groupMemberList,senderId);
    }
    /*
    * 广播消息
    * */
    private void broadcastGroupMessage(JSONObject message, List<GroupMember> memberIds,String sendUserId) {
        JSONObject json = new JSONObject();
        json.set("type", 4)
                .set("groupId", message.getStr("groupId"))
                .set("sendUserId", sendUserId)
                .set("content", message.getStr("content")); //type=4表示群聊发送的消息;
//                .set("sendTime", DateUtil.format(message.getSendTime(), "yyyy-MM-dd HH:mm:ss"));

        memberIds.forEach(memberId -> {
            Session session = sessionMap.get(memberId.getUserId().toString());
            if (session != null && session.isOpen()) {
                sendMessage(json.toString(), session);
            }
        });
    }
}
