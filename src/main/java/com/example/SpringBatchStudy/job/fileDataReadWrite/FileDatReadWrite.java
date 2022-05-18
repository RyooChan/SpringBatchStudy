package com.example.SpringBatchStudy.job.fileDataReadWrite;

import com.example.SpringBatchStudy.job.fileDataReadWrite.dto.Player;
import com.example.SpringBatchStudy.job.fileDataReadWrite.dto.PlayerYears;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * desc : csv파일 저장 및 읽기
 * run: --spring.batch.job.names=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
public class FileDatReadWrite {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep){
        return jobBuilderFactory.get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadWriteStep(ItemReader playerFlatFileItemReader
                                , ItemProcessor playerPlayerYearsItemProcessor
                                , FlatFileItemWriter playerYearsFlatFileItemWriter ){
        return stepBuilderFactory.get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5) // Player 클래스에 담아서 5개의 트랜잭션으로 처리하겠다!
                .reader(playerFlatFileItemReader)
//                .writer(new ItemWriter<Player>() {
//                    @Override
//                    public void write(List<? extends Player> items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(playerPlayerYearsItemProcessor)
                .writer(playerYearsFlatFileItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYears> playerPlayerYearsItemProcessor(){
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return new PlayerYears(item);
            }
        };
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYears> playerYearsFlatFileItemWriter(){
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");   // 콤마로 구분 -> csv로 만들것이기 때문
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Player> playerFlatFileItemReader(){
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("Players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())  // ','를 기준으로 나눔
                .fieldSetMapper(new PlayerFieldSetMapper())
                .linesToSkip(1)     // 맨 위는 데이터가 아니므로 스킵하기 위해 첫번째줄 스킵
                .build();
    }
}
