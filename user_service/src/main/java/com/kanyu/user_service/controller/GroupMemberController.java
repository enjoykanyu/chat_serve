package com.kanyu.user_service.controller;

import com.kanyu.user_service.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/group/member")
public class GroupMemberController {

    @Resource
    private GroupService groupService;
}
