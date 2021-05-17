package com.org.athena.test.service;

public class HomeService {
    RoleService roleService = new RoleService();
    public int doSomething() {
        return 10 + roleService.doSomething();
    }
}
