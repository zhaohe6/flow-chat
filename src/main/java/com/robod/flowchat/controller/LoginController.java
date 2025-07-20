package com.robod.flowchat.controller;

import com.robod.flowchat.common.ResponseBody;
import com.robod.flowchat.entity.UserEntity;
import com.robod.flowchat.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*") // 允许跨域请求
public class LoginController {
    @Autowired
    private UserMapper userMapper; // 假设你有一个 UserMapper 用于操作用户数据
    @PostMapping("/login")
    public ResponseBody login(@RequestParam String username, @RequestParam String password) {
        // 这里可以添加验证逻辑，例如查询数据库验证用户名和密码
        UserEntity user = userMapper.findByUsername(username);
        if (user == null) {
            return ResponseBody.builder()
                    .code(403)
                    .message("User not found")
                    .build();
        }
        if (!user.getPassword().equals(password)) {
            return ResponseBody.builder()
                    .code(401)
                    .message("Invalid password")
                    .build();
        }
        return ResponseBody.builder()
                .code(200)
                .message("Login successful")
                .data(user) // 返回用户信息
                .build();
    }
}
