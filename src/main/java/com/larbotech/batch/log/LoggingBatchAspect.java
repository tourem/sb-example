package com.larbotech.batch.log;

import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_NAME;
import static com.larbotech.batch.log.LogBatch.ContextKeys.METRIC_VALUE;
import static com.larbotech.batch.log.LogBatch.ContextKeys.SOUS_MODULE_LABEL;
import static com.larbotech.batch.log.LogBatch.ContextValues.DEFAULT;
import static com.larbotech.batch.log.LogBatch.ContextValues.SERVICE;

import com.larbotech.batch.enums.Metric;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingBatchAspect {

  @Pointcut("execution(* com.larbotech.batch.service.*.*(..))")
  public void serviceMetier() {
  }

  @Around("serviceMetier()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

    MDC.put(SOUS_MODULE_LABEL.toString(), SERVICE.toString());
    String packageName = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    String args = Arrays.toString(joinPoint.getArgs());

    long debut = System.currentTimeMillis();
    Object sortie = joinPoint.proceed();
    long tempsPasse = System.currentTimeMillis() - debut;

    MDC.put(METRIC_NAME.toString(), Metric.RUNNING_TIME_MS.getName());
    MDC.put(METRIC_VALUE.toString(), String.valueOf(tempsPasse));
    log.info("Exécution du service : {}.{}", packageName, methodName);
    log.trace("Exécution du service arguments : {}.{}({})", packageName, methodName, args);

    MDC.put(SOUS_MODULE_LABEL.toString(), DEFAULT.toString());
    MDC.put(METRIC_NAME.toString(), DEFAULT.toString());
    MDC.put(METRIC_VALUE.toString(), DEFAULT.toString());
    return sortie;
  }

}
