package com.larbotech.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class JobExampleTest {

  @Mock
  private AnnotationConfigApplicationContext context;

  @Test
  public void runJob_statusCompleted() throws Exception {
    // GIVEN
    JobExecution jobExecution = new JobExecution(1L);
    jobExecution.setExitStatus(ExitStatus.COMPLETED);

    org.springframework.batch.core.launch.JobLauncher jobLauncher = mock(
        org.springframework.batch.core.launch.JobLauncher.class);

    when(context.getBean(org.springframework.batch.core.launch.JobLauncher.class)).thenReturn(jobLauncher);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

    // WHEN
    int exitStatus = JobExample.runJob(context);

    // THEN
    assertThat(exitStatus).isEqualTo(0);
  }

  @Test
  public void runJob_statusFailed() throws Exception {
    // GIVEN
    JobExecution jobExecution = new JobExecution(1L);
    jobExecution.setExitStatus(ExitStatus.FAILED);

    org.springframework.batch.core.launch.JobLauncher jobLauncher = mock(
        org.springframework.batch.core.launch.JobLauncher.class);
    when(context.getBean(org.springframework.batch.core.launch.JobLauncher.class)).thenReturn(jobLauncher);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

    // WHEN
    int exitStatus = JobExample.runJob(context);

    // THEN
    assertThat(exitStatus).isEqualTo(1);
  }

  @Test
  public void runJob_exception() throws Exception {
    // GIVEN
    org.springframework.batch.core.launch.JobLauncher jobLauncher = mock(
        org.springframework.batch.core.launch.JobLauncher.class);
    when(context.getBean(org.springframework.batch.core.launch.JobLauncher.class)).thenReturn(jobLauncher);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenThrow(Exception.class);

    // WHEN
    int exitStatus = JobExample.runJob(context);

    // THEN
    assertThat(exitStatus).isEqualTo(1);
  }
}