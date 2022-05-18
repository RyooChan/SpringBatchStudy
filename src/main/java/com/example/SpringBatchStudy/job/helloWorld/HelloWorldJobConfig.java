package com.example.SpringBatchStudy.job.helloWorld;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc : Hello World 출력
 * run: --spring.batch.job.names=helloWorldJob
 */
@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())    // id를 시퀀스로 매번 부여하도록
                .start(helloWorldStep())        // step을 만들어주기 위해, helloWorldStep을 만듬
                .build();
    }

    @JobScope   // Job의 하위에서 실행된다.
    @Bean
    public Step helloWorldStep(){
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(helloWorldTasklet())          // item 안쓸때 이걸 사용한다고 한다.
                .build();
    }

    @StepScope  // step의 하위에서 실행된다.
    @Bean
    public Tasklet helloWorldTasklet(){
        return new Tasklet(){
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception{
                System.out.println("Hello World Spring Batch!!");
                return RepeatStatus.FINISHED;   // 해당 스텝을 끝내준다.
            }
        };
    }
}
