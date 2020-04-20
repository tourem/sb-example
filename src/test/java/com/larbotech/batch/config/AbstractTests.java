package com.larbotech.batch.config;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigurationBatchTests.class})
@TestPropertySource("classpath:context-test-batch.properties")
@ActiveProfiles("test")
public abstract class AbstractTests {

  @Autowired
  @Qualifier("jdbcTemplatePspe")
  protected JdbcTemplate jdbcTemplatePspe;

  @Autowired
  @Qualifier("jdbcTemplateEtp")
  protected JdbcTemplate jdbcTemplateEtp;

  @After
  public void supprimerDonnees() {
    jdbcTemplatePspe.execute("TRUNCATE SCHEMA public AND COMMIT");
    jdbcTemplateEtp.execute("TRUNCATE SCHEMA public AND COMMIT");
  }

}
