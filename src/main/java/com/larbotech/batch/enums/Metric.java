package com.larbotech.batch.enums;

public enum Metric {
  RUNNING_TIME_MS("running_time_ms"),
  USED_MEMORY("used_memory_mo"),
  FREE_MEMORY("free_memory_mo"),
  TOTAL_MEMORY("total_memory_mo"),
  MAX_MEMORY("max_memory_mo"),
  JOB_RUNNING_TIME_SECONDS("job_running_time_seconds"),
  STEP_RUNNING_TIME_SECONDS("step_running_time_seconds"),
  READ_COUNT("read_count"),
  WRITE_COUNT("write_count"),
  COMMIT_COUNT("commit_count"),
  ROLLBACK_COUNT("rollback_count"),
  FILTER_COUNT("filter_count"),
  SKIP_COUNT("skip_count");

  private static final String PREFIX = "nsy_batch_agregat_";

  private String name;

  Metric(String name) {
    this.name = name;
  }

  public String getName() {
    return PREFIX + name;
  }
}
