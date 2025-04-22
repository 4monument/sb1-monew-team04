package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.global.config.S3ConfigProperties;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Resource;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class S3BackupBatch {

  private final EntityManagerFactory emf;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  @Resource(name = "articleS3Resource")
  private final S3Resource articleS3Resource;

  @Bean(name = "s3BackupJob")
  public Job s3BackupJob() {
    return new JobBuilder("s3BackupJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(s3BackupStep())
        .build();
  }

  @Bean
  public Step s3BackupStep() {
    return new StepBuilder("s3BackupStep", jobRepository)
        .<Article, Article>chunk(2, transactionManager)
        .reader(jpaPagingItemReader())
        .writer(s3BackupCustomItemWriter())
        .build();
  }

  // ItemReader : jpaPagingItemReader 기본 구현체 사용
  @Bean
  public JpaPagingItemReader<Article> jpaPagingItemReader() {
    String query = "SELECT a FROM Article a";
    return new JpaPagingItemReaderBuilder<Article>()
        .name("articleJpaPagingItemReader")
        .pageSize(10)
        .entityManagerFactory(emf)
        .queryString(query)
        .build();
  }

  @Bean
  public ItemWriter<Article> s3BackupCustomItemWriter() {
    return items -> {
      try (OutputStream os = articleS3Resource.getOutputStream()) {
        BufferedOutputStream writer = new BufferedOutputStream(os);
        for (Article item : items) {
          writer.write(String.format("%s|%s|%s|%s|%s|%s|%s\n",
              item.getId(),
              item.getSource(),
              item.getSourceUrl(),
              item.getTitle(),
              item.getPublishDate(),
              item.getSummary(),
              item.isDeleted()).getBytes());
        }
      }
    };
    //  private UUID id;
    //  private String source;
    //  private String sourceUrl;
    //  private String title;
    //  private Instant publishDate;
    //  private String summary;
    //  private boolean deleted;
  }

  //  @Bean
//  public FlatFileItemWriter<Article> flatFileItemWriter() {
//    // 문제는
////    String location = "s3://" + s3Properties.bucket() + "/";
//    OutputStream outputStream = null;
//    try {
//      outputStream = articleS3Resource.getOutputStream();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return new FlatFileItemWriterBuilder<Article>()
//        .name("s3BackupWriter")
//        .resource((WritableResource) outputStream)
//        .append(true)
//        .delimited().delimiter("|")  // 나중에 @Value로 바꾸기
//        .names(getFieldNames())
//        .build();
//  }

//  // 하드코딩 해결 나중에 생각하기
//  private String[] getFieldNames() {
//    return new String[]{
//        "id",
//        "source",
//        "sourceUrl",
//        "title",
//        "pulishDate",
//        "summary",
//        "deleted"
//    };
//  }
}
