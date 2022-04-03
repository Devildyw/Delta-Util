package com.dyw.util;

import com.dyw.util.Jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class DeltaUtilApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DeltaUtilApplication.class, args);
        System.out.println(run.getBean("jwtUtil",JwtUtil.class));
    }

}
