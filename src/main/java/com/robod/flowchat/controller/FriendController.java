package com.robod.flowchat.controller;

import com.robod.flowchat.service.FriendService;
import com.robod.flowchat.vo.FriendListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")

public class FriendController {
    @Autowired
    private FriendService friendService;
    @GetMapping("/friendListAndLastMsg")
    public List<FriendListVO> getFriendListAndLastMsg(String username) {
        List<FriendListVO> friendListAndLastMsg = friendService.getFriendListAndLastMsg(username);
        return friendListAndLastMsg;
    }
}
