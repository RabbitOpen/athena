package com.org.athena.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import rabbit.open.athena.plugin.common.context.PluginContext;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class SpringBootEntry {

    public static void main(String[] args) {
        System.out.println(PluginContext.getContext().getMetaData().getApplicationName());
        SpringApplication.run(SpringBootEntry.class);
    }
}
