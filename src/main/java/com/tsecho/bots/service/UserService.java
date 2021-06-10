package com.tsecho.bots.service;

import com.tsecho.bots.model.common.User;
import com.tsecho.bots.repository.common.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void addUser(User user) {
        userRepository.save(user);
    }

    public User getUser(Long userid) throws EntityNotFoundException {
        return userRepository.getOne(userid);
    }

    public List<User> findAllUsers(Long userid){
        return userRepository.getAllById(userid);
    }
}
