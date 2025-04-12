package com.kanyu.chat.controller;

import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.Group;
import com.kanyu.chat.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Resource
    private GroupService groupService;

    // 创建群组
    @PostMapping("create")
    public Result createGroup(@RequestBody GroupDto dto) {
        Group group = groupService.createGroup(dto);
        return Result.ok(group);
    }

    // 解散群组
    @PostMapping("/deleteGroup")
    public Result deleteGroup(@RequestBody Group dto) {
        return groupService.deleteGroup(dto);
    }

    // 更新群名称
    @PostMapping("/updateGroup")
    public Result updateGroup(@RequestBody Group dto) {
        return groupService.updateGroup(dto);
    }
}
