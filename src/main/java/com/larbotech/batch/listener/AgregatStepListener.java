package com.larbotech.batch.listener;

import static com.larbotech.batch.log.LogBatch.ContextKeys.MODULE_LABEL;

import com.larbotech.batch.log.LogBatch.ContextValues;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class AgregatStepListener implements StepExecutionListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    MDC.put(MODULE_LABEL.toString(), stepExecution.getStepName());
    log.info("before step: {}", stepExecution);
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    log.info("after step: {}", stepExecution);
    MDC.put(MODULE_LABEL.toString(), ContextValues.DEFAULT.toString());
    return null;
  }
}
