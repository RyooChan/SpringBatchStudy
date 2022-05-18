package com.example.SpringBatchStudy;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling	// 특정 시간마다 배치를 돌려주기 위해서
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchStudyApplication.class, args);
	}

}
