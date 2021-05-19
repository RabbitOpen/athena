package com.org.athena.test.service;

public class BaseService {

    RoleService roleService = new RoleService();
    HomeService homeService = new HomeService();

    public int doSomething(int age, String name) {

        int r1 = roleService.doSomething();
        int r2 = homeService.doSomething();
        return 10 + r1 + r2;
    }
}
