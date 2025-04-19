package com.kanyu.user_service.controller;

import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.GroupDto;
import com.kanyu.user_service.entity.Group;
import com.kanyu.user_service.service.GroupService;
import org.springframework.web.bind.annotation.*;

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

    //查看群成员信息
    // 更新群名称
    @GetMapping("/getGroupMember")
    public Result getGroupMember(@RequestParam String groupId) {
        return groupService.getGroupMember(groupId);
    }

}
