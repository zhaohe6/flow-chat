package com.robod.flowchat.vo;

import lombok.Data;

@Data
public class FriendListVO {
    /*用户id*/
    private String id;
    /* 用户名*/
    private String username;

    /*好友 名字*/
    private String friendName;

    /*用户头像*/
    private String avatar = null;

    /*最后一条消息*/
    private String lastMessage;

    /*最后一条消息时间*/
    private String lastTime;

    /*未读消息数*/
    private Integer unreadCount = 0;

    /*是否在线*/
    private Boolean online = false;
}
