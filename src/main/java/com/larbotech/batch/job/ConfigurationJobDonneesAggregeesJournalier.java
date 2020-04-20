package com.larbotech.batch.job;

import com.larbotech.batch.config.ConfigurationBatch;
import com.larbotech.batch.exception.ServiceEntrepotException;
import com.larbotech.batch.exception.TypeStepExecutableInvalideException;
import com.larbotech.batch.listener.AgreagatJobListener;
import com.larbotech.batch.listener.AgregatProcessListener;
import com.larbotech.batch.listener.AgregatReadListener;
import com.larbotech.batch.listener.AgregatSkipListener;
import com.larbotech.batch.listener.AgregatStepListener;
import com.larbotech.batch.listener.AgregatWriteListener;
import com.larbotech.batch.metier.CanalSource;
import com.larbotech.batch.modele.DonneesCanalBatch;
import com.larbotech.batch.partition.DateRangePartitioner;
import com.larbotech.batch.processor.AgregationProcessor;
import com.larbotech.batch.processor.CanalBatchProcessor;
import com.larbotech.batch.reader.CanalBatchJdbcCursorReader;
import com.larbotech.batch.reader.CanalSourceJdbcCursorReader;
import com.larbotech.batch.writer.CanalBatchCompositeWriter;
import com.larbotech.batch.writer.DonneesCanalBatchCompositeWriter;
import com.larbotech.batch.metier.CanalBatch;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@Import(ConfigurationBatch.class)
public class ConfigurationJobDonneesAggregeesJournalier {

  public static final String JOB_NAME = "jobDonneesAggregeesJournalier";

  public static final String STEP_DONNEES_AGREGAT_JOUR = "stepDonneesAgregatJour";
  public static final String STEP_INIT = "stepInit";
  public static final String STEP_MASTER = "stepMaster";

  @Value("${step.executable.type}")
  private TypeStepExecutable typeStepExecutable;

  @Value("${step.init.chunk.size}")
  private int stepInitChunkSize;

  @Value("${step.agregat.chunk.size}")
  private int stepAgregatChunkSize;

  @Value("${step.agregat.skip.limit}")
  private int skipLimit;

  @Value("${step.agregat.corePoolSize}")
  private int corePoolSize;

  @Value("${step.agregat.maxPoolSize}")
  private int maxPoolSize;

  @Autowired
  @Qualifier("stepBuilderFactory")
  private StepBuilderFactory steps;

  @Autowired
  private JobBuilderFactory jobBuilderFactory;


  @Bean(name = JOB_NAME)
  public Job donneesAggregeesJournalierJob() throws Exception {
    Job job;

    switch (typeStepExecutable) {
      case INIT:
        job = start(stepInit())
            .build();
        break;
      case AGREGAT:
        job = start(stepMaster())
            .build();
        break;
      case ALL:
        job = start(stepInit())
            .next(stepMaster())
            .build();
        break;
      default:
        throw new TypeStepExecutableInvalideException(
            "Le type de step à exécuter est invalide : " + typeStepExecutable);
    }

    ((SimpleJob) job).setRestartable(false);
    return job;
  }

  private SimpleJobBuilder start(Step step) {
    return jobBuilderFactory.get(JOB_NAME)
        .listener(new AgreagatJobListener())
        .start(step);
  }


  @Bean(name = STEP_INIT)
  @SuppressWarnings("unchecked")
  public Step stepInit() {
    return steps.get(STEP_INIT)
        .listener(new AgregatStepListener())
        .<CanalSource, ListeCanalPeriodeDecoupee>chunk(stepInitChunkSize)
        .reader(canalSourceJdbcCursorReader)
        .processor(canalBatchProcessor)
        .writer(canalBatchCompositeWriter)
        .listener(new AgregatReadListener())
        .listener(new AgregatProcessListener())
        .listener(new AgregatWriteListener())
        .listener(new AgregatSkipListener())
        .build();
  }

  @Bean(name = STEP_DONNEES_AGREGAT_JOUR)
  @SuppressWarnings("unchecked")
  public Step slaveStepDonneesAgregatJour() {
    return steps.get(STEP_DONNEES_AGREGAT_JOUR)
        .listener(new AgregatStepListener())
        .<CanalBatch, DonneesCanalBatch>chunk(stepAgregatChunkSize)
        .reader(canalBatchJdbcCursorReader)
        .processor(agregationProcessor)
        .writer(donneesCanalBatchCompositeWriter)
        .listener(new AgregatReadListener())
        .listener(new AgregatProcessListener())
        .listener(new AgregatWriteListener())
        .listener(new AgregatSkipListener())
        .faultTolerant()
        .skip(ServiceEntrepotException.class)
        .skipLimit(skipLimit)
        .build();
  }

  @Bean(name = STEP_MASTER)
  public Step stepMaster() throws Exception {
    return steps.get(STEP_MASTER)
        .partitioner(slaveStepDonneesAgregatJour().getName(), dateRangePartitioner)
        .partitionHandler(masterSlaveHandler()).build();
  }

  @Bean
  public PartitionHandler masterSlaveHandler() throws Exception {
    TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
    handler.setGridSize(corePoolSize);
    handler.setTaskExecutor(taskExecutor());
    handler.setStep(slaveStepDonneesAgregatJour());
    handler.afterPropertiesSet();
    return handler;
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
    threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
    return threadPoolTaskExecutor;
  }
}
