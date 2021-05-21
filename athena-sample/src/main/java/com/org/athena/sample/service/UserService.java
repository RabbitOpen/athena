package com.org.athena.sample.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("user-server")
public interface UserService {

    @PostMapping("/user/getUserName")
    String getUserName();

    @RequestMapping("/user/getUser")
    String getUser();


}
