package com.workable.movieramatestapp.service;

import com.workable.movieramatestapp.domain.User;

import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    Optional<User> findUser(Long id);

}
