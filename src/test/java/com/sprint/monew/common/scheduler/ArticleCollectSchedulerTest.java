package com.sprint.monew.common.scheduler;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.ARTICLE_IDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleCollectSchedulerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job articleCollectJob;

  @Mock
  private Job notificationCreateJob;

  @InjectMocks
  private ArticleCollectScheduler scheduler;

  @BeforeEach
  void setUp() {
    // @Resource로 주입되는 필드를 수동으로 설정
    ReflectionTestUtils.setField(scheduler, "articleCollectJob", articleCollectJob);
    ReflectionTestUtils.setField(scheduler, "notificationCreateJob", notificationCreateJob);
  }

  @Test
  @DisplayName("스케줄러가 올바른 Job을 순서대로 실행하는지 테스트")
  void collectArticlesTest() throws Exception {
    // given
    JobExecution mockExecution = mock(JobExecution.class);
    ExecutionContext mockExecutionContext = mock(
        ExecutionContext.class); // ExecutionContext도 mock으로 변경

    // Mock ExecutionContext 설정
    when(mockExecution.getExecutionContext()).thenReturn(mockExecutionContext);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mockExecution);

    // when
    scheduler.collectArticles();

    // then
    // Job 실행 순서 및 횟수 검증
    verify(jobLauncher, times(2)).run(any(Job.class), any(JobParameters.class));

    // ExecutionContext에서 ARTICLE_IDS가 제거되었는지 검증
    verify(mockExecutionContext).remove(ARTICLE_IDS.getKey());
  }
}