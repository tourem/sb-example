package com.larbotech.batch.listener;

import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class AgregatSkipListener implements SkipListener {

  @Override
  public void onSkipInRead(Throwable t) {
    log.warn("skip read", t);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }

  @Override
  public void onSkipInProcess(Object item, Throwable t) {
    log.warn("skip process item: {}", item, t);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }

  @Override
  public void onSkipInWrite(Object item, Throwable t) {
    log.warn("skip write item: {}", item, t);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }
}
