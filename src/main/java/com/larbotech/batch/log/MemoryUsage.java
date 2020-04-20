package com.larbotech.batch.log;

import static com.larbotech.batch.enums.Metric.FREE_MEMORY;
import static com.larbotech.batch.enums.Metric.MAX_MEMORY;
import static com.larbotech.batch.enums.Metric.TOTAL_MEMORY;
import static com.larbotech.batch.enums.Metric.USED_MEMORY;
import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.MEMORY;
import static com.larbotech.batch.log.LogHelper.logMetric;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class MemoryUsage {

  private static final int MO = 1024 * 1024;

  private MemoryUsage() {
  }

  public static void logStatistics() {
    MDC.put(SOUS_MODULE_LABEL.toString(), MEMORY.toString());

    Runtime runtime = Runtime.getRuntime();

    log.info("Heap utilization statistics");
    logMetric(USED_MEMORY.getName(), String.valueOf((runtime.totalMemory() - runtime.freeMemory()) / MO));
    logMetric(FREE_MEMORY.getName(), String.valueOf(runtime.freeMemory() / MO));
    logMetric(TOTAL_MEMORY.getName(), String.valueOf(runtime.totalMemory() / MO));
    logMetric(MAX_MEMORY.getName(), String.valueOf(runtime.maxMemory() / MO));

    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
  }
}
