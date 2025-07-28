package com.robod.flowchat.controller;

import com.robod.flowchat.common.ResponseBody;
import com.robod.flowchat.entity.UserEntity;
import com.robod.flowchat.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/registerFork")
    public ResponseBody<String> registerFork(){
//        List<UserEntity> users = new ArrayList<>();
        for(int i=0;i<3000;i++){
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername("testUser_"+i);
            userEntity.setPassword("123456");
            userMapper.insert(userEntity);
        }

        return ResponseBody.<String>builder().code(200).data(null).message("成功插入模拟用户数据").build();
    }
}
