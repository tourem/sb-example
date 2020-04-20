package com.larbotech.batch.listener;


import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.WRITER;

import com.larbotech.batch.log.MemoryUsage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.ItemWriteListener;

@Slf4j
public class AgregatWriteListener implements ItemWriteListener {

  @Override
  public void beforeWrite(List items) {
    MDC.put(SOUS_MODULE_LABEL.toString(), WRITER.toString());
    log.info("before write items: {}", items);
    MemoryUsage.logStatistics();
  }

  @Override
  public void afterWrite(List items) {
    log.info("after write items: {}", items);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
    MemoryUsage.logStatistics();
  }

  @Override
  public void onWriteError(Exception exception, List items) {
    log.error("error write items: {}", items, exception);
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
    MemoryUsage.logStatistics();
  }
}
