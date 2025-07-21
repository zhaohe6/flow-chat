package com.robod.flowchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robod.flowchat.entity.FriendRelationEntity;
import com.robod.flowchat.vo.FriendListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface FriendMapper extends BaseMapper<FriendRelationEntity> {
    @Select("select  last_message.id as id,username,friend as friendName,content as lastMessage,timestamp as lastTime from t_friend_relation " +
            "left join (select * from t_message where (sender, receiver, timestamp) in (" +
            "    select sender, receiver, max(timestamp) from t_message where receiver = #{username} group by sender, receiver " +
            ")) as last_message on t_friend_relation.username = last_message.receiver and t_friend_relation.friend = last_message.sender " +
            "                         where username=#{username};")
    List<FriendListVO> getFriendList(@Param("username") String username);
}
