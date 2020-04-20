package com.larbotech.batch.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateHelper {

  public static final String TIME_ZONE_FR = "Europe/Paris";
  private static final ZoneId ZONE_PARIS = ZoneId.of(TIME_ZONE_FR);
  private static final ZoneId ZONE_UTC = ZoneId.of("UTC");

  private DateHelper() {
  }

  public static LocalDateTime fromUTCtoParis(LocalDateTime dateTime) {
    return dateTime.atZone(ZONE_UTC).withZoneSameInstant(ZONE_PARIS).toLocalDateTime();
  }

  public static LocalDateTime fromParistoUTC(LocalDateTime dateTime) {
    return dateTime.atZone(ZONE_PARIS).withZoneSameInstant(ZONE_UTC).toLocalDateTime();
  }

  public static LocalDateTime fromUTCtoParisAtMidnight(LocalDateTime dateTime) {
    return fromUTCtoParis(dateTime).toLocalDate().atStartOfDay();
  }

  public static LocalDateTime atEndOfDay(LocalDateTime dateTime) {
    return dateTime.toLocalDate().atTime(23, 59, 59);
  }

  public static LocalDate convertDateToLocalDate(Date date) {
    Instant instant = date.toInstant();
    return instant.atZone(ZONE_PARIS).toLocalDate();
  }

  public static Date convertLocalDateTimeToDate(LocalDateTime date) {
    Instant instant = date.atZone(ZoneOffset.UTC).toInstant();
    return Date.from(instant);
  }

  public static long durationTimeInSeconds(Date startTime, Date endTime) {
    return Duration.between(startTime.toInstant(), endTime.toInstant()).getSeconds();
  }

  public static LocalDate minLocalDate(LocalDate date1, LocalDate date2){
    if (date2 == null) return date1;
    if (date2.isBefore(date1)) return date2;
    return date1;
  }
}
