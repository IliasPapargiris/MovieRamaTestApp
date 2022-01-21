package com.workable.movieramatestapp.service;

import com.workable.movieramatestapp.dao.UserDao;
import com.workable.movieramatestapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    public final UserDao userDao;


    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        if (userDao.existsByUsername(user.getUsername())) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userDao.saveAndFlush(user);
        return saved;
    }

    @Override
    public Optional<User> findUser(Long userId) {
        return userDao.findById(userId);
    }


}
