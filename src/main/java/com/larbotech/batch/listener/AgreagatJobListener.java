package com.larbotech.batch.listener;

import static com.larbotech.batch.enums.Metric.COMMIT_COUNT;
import static com.larbotech.batch.enums.Metric.FILTER_COUNT;
import static com.larbotech.batch.enums.Metric.JOB_RUNNING_TIME_SECONDS;
import static com.larbotech.batch.enums.Metric.READ_COUNT;
import static com.larbotech.batch.enums.Metric.ROLLBACK_COUNT;
import static com.larbotech.batch.enums.Metric.SKIP_COUNT;
import static com.larbotech.batch.enums.Metric.STEP_RUNNING_TIME_SECONDS;
import static com.larbotech.batch.enums.Metric.WRITE_COUNT;
import static com.larbotech.batch.log.LogBatch.ContextKeys.APPLICATION_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextKeys.MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.NOM_APPLICATION;
import static com.larbotech.batch.log.LogBatch.ContextValues.RECAP;
import static com.larbotech.batch.log.LogHelper.logMetric;

import com.larbotech.batch.log.LogBatch;
import com.larbotech.batch.util.DateHelper;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

@Slf4j
public class AgreagatJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    MDC.put(APPLICATION_LABEL.toString(), NOM_APPLICATION.toString());
    log.info("before job: {}", jobExecution);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("after job: {}", jobExecution);

    MDC.put(SOUS_MODULE_LABEL.toString(), RECAP.toString());

    log.info("Synthese job {}", jobExecution.getJobInstance().getJobName());

    Date jobStartTime = jobExecution.getStartTime();
    Date jobEndTime = jobExecution.getEndTime();
    log.info("job started {}", jobStartTime);
    log.info("job finished {}", jobEndTime);
    logMetric(JOB_RUNNING_TIME_SECONDS.getName(),
        String.valueOf(DateHelper.durationTimeInSeconds(jobStartTime, jobEndTime)));

    log.info("job status {}", jobExecution.getStatus());

    jobExecution.getJobParameters().getParameters()
        .forEach((key, value) -> log.info("Job parameter {}={}", key, value));

    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      MDC.put(MODULE_LABEL.toString(), stepExecution.getStepName());
      logMetric(STEP_RUNNING_TIME_SECONDS.getName(),
          String.valueOf(DateHelper.durationTimeInSeconds(stepExecution.getStartTime(), stepExecution.getEndTime())));

      logMetric(READ_COUNT.getName(), String.valueOf(stepExecution.getReadCount()));
      logMetric(WRITE_COUNT.getName(), String.valueOf(stepExecution.getWriteCount()));
      logMetric(COMMIT_COUNT.getName(), String.valueOf(stepExecution.getCommitCount()));
      logMetric(SKIP_COUNT.getName(), String.valueOf(stepExecution.getSkipCount()));
      logMetric(ROLLBACK_COUNT.getName(), String.valueOf(stepExecution.getRollbackCount()));
      logMetric(FILTER_COUNT.getName(), String.valueOf(stepExecution.getFilterCount()));
    }
    LogBatch.setDefaultMDCValue();
  }
}
