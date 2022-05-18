package com.example.SpringBatchStudy.job.dbDataReadWrite;

import com.example.SpringBatchStudy.core.domain.accounts.Accounts;
import com.example.SpringBatchStudy.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchStudy.core.domain.orders.Orders;
import com.example.SpringBatchStudy.core.domain.orders.OrdersRepository;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * Item 내용들을 사용해 제작!!
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep){
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrderProcessor, ItemWriter trOrderWriter){
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Orders>chunk(5)   // 5개의 사이즈만큼 처리한 후에 데이터를 처리하겠다! <- 5개의 데이터 단위로 처리한다.
                // 읽어온 데이터는 Orders타입, write할것도 Orders타입
                .reader(trOrdersReader)       // 데이터를 읽어오는 Reader
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor)
                .writer(trOrderWriter)
                .build();
    }

//    @Bean
//    @StepScope
//    public RepositoryItemWriter<Accounts> trOrdersWriter(){
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

    // 위의 내용과 완전히 동일한 기능이다.
    // 직접 구현을 해서 save처리함.
    @Bean
    @StepScope
    public ItemWriter<Accounts> trOrdersWriter(){
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<Orders, Accounts> trOrderProcessor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Orders> trOrdersReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
