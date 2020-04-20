package com.larbotech.batch.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Test;

public class DateHelperTest {

  private static final LocalDateTime DATE_2018_3_1_23h = LocalDateTime.of(2018, 3, 1, 23, 0);
  private static final LocalDateTime DATE_2018_3_2 = LocalDateTime.of(2018, 3, 2, 0, 0);
  private static final LocalDateTime DATE_2018_3_2_12h = LocalDateTime.of(2018, 3, 2, 12, 0);

  private static final LocalDateTime DATE_2018_7_1_22h = LocalDateTime.of(2018, 7, 1, 22, 0);
  private static final LocalDateTime DATE_2018_7_2 = LocalDateTime.of(2018, 7, 2, 0, 0);

  private static final LocalDateTime DATE_2015_10_13 = LocalDateTime.of(2015, 10, 13,0,0);
  private static final LocalDateTime DATE_2015_10_13_13h = LocalDateTime.of(2015, 10, 13,13,0);
  private static final LocalDateTime DATE_2015_10_13_23h59m59s = LocalDateTime.of(2015, 10, 13, 23, 59, 59);

  @Test
  public void fromUTCtoParis() {
    assertThat(DateHelper.fromUTCtoParis(DATE_2018_3_1_23h)).isEqualTo(DATE_2018_3_2);
    assertThat(DateHelper.fromUTCtoParis(DATE_2018_7_1_22h)).isEqualTo(DATE_2018_7_2);
  }

  @Test
  public void fromUTCtoParisAtMidnight() {
    assertThat(DateHelper.fromUTCtoParisAtMidnight(DATE_2018_3_2_12h)).isEqualTo(DATE_2018_3_2);
  }

  @Test
  public void fromParistoUTC() {
    assertThat(DateHelper.fromParistoUTC(DATE_2018_3_2)).isEqualTo(DATE_2018_3_1_23h);
    assertThat(DateHelper.fromParistoUTC(DATE_2018_7_2)).isEqualTo(DATE_2018_7_1_22h);
  }

  @Test
  public void atEndOfDay() {
    assertThat(DateHelper.atEndOfDay(DATE_2015_10_13_13h)).isEqualTo(DATE_2015_10_13_23h59m59s);
  }

  @Test
  public void atEndOfDay_quandDateAMinuit() {
    assertThat(DateHelper.atEndOfDay(DATE_2015_10_13)).isEqualTo(DATE_2015_10_13_23h59m59s);
  }

  @Test
  public void durationTimeInSeconds() {
    long expectedDuration = 12L;
    Instant start = DATE_2018_3_2.toInstant(ZoneOffset.UTC);
    Instant end = start.plusSeconds(expectedDuration);

    assertThat(DateHelper.durationTimeInSeconds(Date.from(start), Date.from(end))).isEqualTo(
        expectedDuration);
  }
}