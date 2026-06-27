package com.bu.anime_web.helper;

import com.bu.anime_web.entity.User;
import com.bu.anime_web.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {
    @Autowired
    private IUserRepository userRepository;
    public User getUser(Long userId) {
        return userRepository.findById(userId).get();
    }
}
