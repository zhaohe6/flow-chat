package com.robod.flowchat.controller;

import com.robod.flowchat.common.ResponseBody;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageService messageService;
    /**
     * 获取用户与好友之间的消息记录
     * @param username 用户名
     * @param friendName 好友用户名
     * @return 消息列表
     */
    @GetMapping("/getFriendMessage")
    public ResponseBody<List<MsgEntity>> getFriendMessage(@RequestParam String username, @RequestParam String friendName)  {
        List<MsgEntity> friendMessage = messageService.getFriendMessage(username, friendName);


        return ResponseBody.<List<MsgEntity>>builder()
                .code(200)
                .message("获取好友消息成功")
                .data(friendMessage) // 这里可以设置为null或其他默认值
                .build();

    }
}
