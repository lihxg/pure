package com.pure.pureroot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pure.pureroot.mapper")
public class PureRootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PureRootApplication.class, args);
    }

}
