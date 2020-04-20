package com.larbotech.batch;

import static com.larbotech.batch.log.LogBatch.setDefaultMDCValue;

import com.larbotech.batch.job.ConfigurationJobDonneesAggregeesJournalier;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class JobExample {

  public static final String TIMESTAMP_PARAM_NAME = "date";
  private static final int FAILED = 1;

  public static void main(String[] args) {

    setDefaultMDCValue();
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        ConfigurationJobDonneesAggregeesJournalier.class);

    int exitStatus = runJob(context);
    System.exit(exitStatus);
  }

  static int runJob(AnnotationConfigApplicationContext context) {
    int exitStatus;

    Date date = new Date();
    JobParameters param = new JobParametersBuilder().addDate(TIMESTAMP_PARAM_NAME, date)
        .toJobParameters();

    org.springframework.batch.core.launch.JobLauncher jobLauncher = context.getBean(
        org.springframework.batch.core.launch.JobLauncher.class);
    Job job = (Job) context.getBean(ConfigurationJobDonneesAggregeesJournalier.JOB_NAME);
    try {
      ExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
      JobExecution jobExecution = jobLauncher.run(job, param);
      exitStatus = exitCodeMapper.intValue(jobExecution.getExitStatus().getExitCode());
    } catch (Throwable throwable) {
      exitStatus = FAILED;
      String message = "Une erreur inatendue a été détéctée: " + throwable.getMessage();
      log.error(message, throwable);
    } finally {
      context.close();
    }
    return exitStatus;
  }
}
