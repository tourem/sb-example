package com.larbotech.batch.log;

import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_NAME;
import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_VALUE;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class LogHelper {

  private LogHelper() {
  }

  public static void logMetric(String name, String value) {
    MDC.put(METRIC_NAME.toString(), name);
    MDC.put(METRIC_VALUE.toString(), value);
    log.info("");
    MDC.put(METRIC_NAME.toString(), DEFAULT.toString());
    MDC.put(METRIC_VALUE.toString(), DEFAULT.toString());
  }

}
