package com.org.athena.test.bean;

import com.org.athena.test.service.RoleService;
import com.org.athena.test.service.UserService;

public class SimpleService {

    UserService us = new UserService();
    RoleService roleService = new RoleService();

    public void doSomething() {
        us.doSomething(10, "");
        roleService.doSomething();
    }
}
