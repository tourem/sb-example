package com.larbotech.batch.listener;

import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.READER;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class AgregatReadListener implements ItemReadListener {

  @Override
  public void beforeRead() {
    MDC.put(SOUS_MODULE_LABEL.toString(), READER.toString());
    log.info("before read");
  }

  @Override
  public void afterRead(Object item) {
    log.info("after read item: {}", item);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }

  @Override
  public void onReadError(Exception ex) {
    log.error("error read", ex);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }
}
