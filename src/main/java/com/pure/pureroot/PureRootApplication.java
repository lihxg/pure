package com.pure.pureroot;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pure.pureroot.logging.ArmsAgent;

@SpringBootApplication
@MapperScan("com.pure.pureroot.mapper")
public class PureRootApplication {

	private static final Logger log = LoggerFactory.getLogger(PureRootApplication.class);

	public static void main(String[] args) {
		log.info("ARMS agent present: {}", ArmsAgent.isPresent());
		SpringApplication.run(PureRootApplication.class, args);
	}

}
