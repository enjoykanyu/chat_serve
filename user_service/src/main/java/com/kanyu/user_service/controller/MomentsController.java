package com.kanyu.user_service.controller;

import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 朋友圈控制器
 */
@Slf4j
@RestController
@RequestMapping("/moments")
public class MomentsController {



    //发布朋友圈
    @PostMapping("release")
    public Result momentsRelease(@RequestParam("sendUserId") String sendUserId, @RequestParam("receiveUserId") String receiveUserId, HttpSession session) {
       return Result.ok();
    }
    //修改朋友圈
    @PostMapping("update")
    public Result momentsUpdate(@RequestParam("sendUserId") String sendUserId, @RequestParam("searchUserName") String searchUserName, HttpSession session) {
        return Result.ok();
    }

    //删除朋友圈
    @PostMapping("delete")
    public Result momentsDelete(@RequestBody MessageDto messageDto, HttpSession session) {

        return Result.ok();
    }

    //评论朋友圈
    @PostMapping("comment")
    public Result momentsComment(HttpSession session) {
        return Result.ok();
    }

    //点赞朋友圈
    @PostMapping("like")
    public Result momentsLike(@RequestParam("groupId") String group_id, HttpSession session) {
        return Result.ok();
    }

    //查看当前用户可以看到的朋友圈
    @GetMapping("moments/list")
    public Result momentsList(@RequestParam("groupId") String group_id, HttpSession session) {
        return Result.ok();
    }
    //朋友圈详情
    @GetMapping("moments/get")
    public Result momentsGet(@RequestParam("groupId") String group_id, HttpSession session) {
        return Result.ok();
    }
}
