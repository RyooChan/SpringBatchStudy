package com.example.SpringBatchStudy.job.validatedParam;

import com.example.SpringBatchStudy.job.validatedParam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


/**
 * desc : 파일 이름 파라미터 전달 및 검증
 * run: --spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job validatedParamJob(Step validatedParamStep){
        return jobBuilderFactory.get("validatedParamJob")
                .incrementer(new RunIdIncrementer())    // id를 시퀀스로 매번 부여하도록
//                .validator(new FileParamValidator())
                .validator(multipleValidator())
                .start(validatedParamStep)        // step을 만들어주기 위해, helloWorldStep을 만듬
                .build();
    }

    // 다수의 validator을 사용할 때에
    private CompositeJobParametersValidator multipleValidator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @JobScope   // Job의 하위에서 실행된다.
    @Bean
    public Step validatedParamStep(Tasklet validatedParamTasklet){
        return stepBuilderFactory.get("validatedParamStep")
                .tasklet(validatedParamTasklet)          // item 안쓸때 이걸 사용한다고 한다.
                .build();
    }

    @StepScope  // step의 하위에서 실행된다.
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName){
        return new Tasklet(){
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception{
                System.out.println(fileName);
                System.out.println("validated Param Tasklet!!");
                return RepeatStatus.FINISHED;   // 해당 스텝을 끝내준다.
            }
        };
    }
}
