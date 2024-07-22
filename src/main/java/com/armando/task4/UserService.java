package com.armando.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationTime(LocalDateTime.now());
        user.setBlocked(false);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void blockUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setActive(false);
                userRepository.save(user);
            }
        }
    }

    public void unblockUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setActive(true);
                userRepository.save(user);
            }
        }
    }

    public void deleteUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            userRepository.deleteById(userId);
        }
    }
}