package com.kanyu.chat.config;

import com.kanyu.chat.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    @Autowired
    private GroupMemberService groupMemberService;
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        WebSocketServe.setGroupMemberService(groupMemberService);
        return new ServerEndpointExporter();
    }
}
