package com.larbotech.batch.log;

import static com.larbotech.batch.log.LogBatch.ContextKeys.APPLICATION_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_NAME;
import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_VALUE;
import static com.larbotech.batch.log.LogBatch.ContextKeys.MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.NOM_APPLICATION;

import org.slf4j.MDC;

public interface LogBatch {

  enum ContextKeys {
    APPLICATION_LABEL("APPLICATION"),
    MODULE_LABEL("MODULE"),
    SOUS_MODULE_LABEL("SOUS_MODULE"),
    METRIC_NAME("METRIC_NAME"),
    METRIC_VALUE("METRIC_VALUE");

    private String key;

    ContextKeys(String key) {
      this.key = key;
    }

    @Override
    public String toString() {
      return key;
    }
  }

  enum ContextValues {
    NOM_APPLICATION("BATCH_AGREGAT_JOUR"),
    READER("READER"),
    PROCESSOR("PROCESSOR"),
    WRITER("WRITER"),
    SERVICE("SERVICE"),
    RECAP("RECAP"),
    MEMORY("MEMORY"),
    DEFAULT("-");

    private String value;

    ContextValues(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  static void setDefaultMDCValue() {
    MDC.put(APPLICATION_LABEL.toString(), NOM_APPLICATION.toString());
    MDC.put(MODULE_LABEL.toString(), DEFAULT.toString());
    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
    MDC.put(METRIC_NAME.toString(), DEFAULT.toString());
    MDC.put(METRIC_VALUE.toString(), DEFAULT.toString());
  }

}
