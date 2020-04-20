package com.larbotech.batch.listener;

import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.PROCESSOR;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class AgregatProcessListener implements ItemProcessListener {

  @Override
  public void beforeProcess(Object item) {
    MDC.put(SOUS_MODULE_LABEL.toString(), PROCESSOR.toString());
    log.info("before process item: {}", item);
  }

  @Override
  public void afterProcess(Object item, Object result) {
    log.info("after process item: {} result: {}", item, result);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }

  @Override
  public void onProcessError(Object item, Exception e) {
    log.error("error process item: {}", item, e);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }
}
