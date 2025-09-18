package com.robod.flowchat.config;

import com.robod.flowchat.entity.UserEntity;
import com.robod.flowchat.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper  userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userMapper.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在"+username);
        }

        return new User(user.getUsername(),"{noop}" + user.getPassword(), List.of());
    }
}
