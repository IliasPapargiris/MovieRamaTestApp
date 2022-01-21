package com.workable.movieramatestapp.service;

import com.workable.movieramatestapp.dao.UserDao;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Not found username:"));
        return new MyUserDetails(userDao.findByUsername(username).get());
    }
}
