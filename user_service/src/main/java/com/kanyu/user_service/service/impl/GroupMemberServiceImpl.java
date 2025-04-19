package com.kanyu.user_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.user_service.entity.GroupMember;
import com.kanyu.user_service.mapper.GroupMemberMapper;
import com.kanyu.user_service.service.GroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {



    @Override
    public void createGroupMember(String uuid, Long userId,Integer role) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(uuid);
        groupMember.setUserId(userId);
        groupMember.setRole(role);
        save(groupMember);
    }

    /*
    * 查询当前用户加入的所有群聊
    * */
    @Override
    public List<GroupMember> selectGroups(Long userId) {
        List<GroupMember> groups = query().eq("user_id", userId).list();
        return groups;
    }


    /*
     * 查询当前用户是否为群成员
     * */
    @Override
    public Boolean isGroupMember(String uuid, Long userId) {
        GroupMember member = query().eq("group_id", uuid).eq("user_id", userId).one();
        if (member==null) {
            return false;
        }else if (member.getStatus()==1){
            return true;
        }else {
         return false;
        }
    }
}
