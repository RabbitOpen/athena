package com.org.athena.sample.component;

import com.org.athena.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class User {

    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        System.out.println(userService.getUserName());
    }

}
