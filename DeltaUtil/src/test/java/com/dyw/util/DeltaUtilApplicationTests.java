package com.dyw.util;

import com.dyw.util.Jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeltaUtilApplicationTests {

    @Test
    void contextLoads() {
        String dfasf = JwtUtil.generateToken("dfasf");
        System.out.println(dfasf);
    }
}
