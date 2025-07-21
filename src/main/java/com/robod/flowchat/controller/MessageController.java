package com.robod.flowchat.controller;

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
    @GetMapping("/getFriendMessage")
    public List<MsgEntity> getFriendMessage(@RequestParam String username, @RequestParam String friendName)  {
        return messageService.getFriendMessage(username, friendName);
    }
}
