package com.larbotech.batch.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Import(ConfigurationSourcesPlaceholder.class)
public class ConfigurationDatasource {

  @Value("${entrepot.bd.driverClassName}")
  private String entrepotDriverClassName;
  @Value("${entrepot.bd.password}")
  private String entrepotPassword;
  @Value("${entrepot.bd.url}")
  private String entrepotJdbcUrl;
  @Value("${entrepot.bd.username}")
  private String entrepotUsername;

  @Value("${pspe.bd.driverClassName}")
  private String pspeDriverClassName;
  @Value("${pspe.bd.password}")
  private String pspePassword;
  @Value("${pspe.bd.url}")
  private String pspeJdbcUrl;
  @Value("${pspe.bd.username}")
  private String pspeUsername;

  @Bean
  public DataSource dataSource() {
    return getDriverManagerDataSource(
        entrepotDriverClassName, entrepotJdbcUrl, entrepotUsername, entrepotPassword);
  }

  @Bean(name = "jdbcTemplatePspe")
  public JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(dataSourcePspe());
  }

  @Bean(name = "jdbcTemplateEtp")
  public JdbcTemplate getJdbcTemplateEtp() {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  public DataSource dataSourcePspe() {
    return getDriverManagerDataSource(pspeDriverClassName, pspeJdbcUrl, pspeUsername, pspePassword);
  }

  private DriverManagerDataSource getDriverManagerDataSource(String valorisationDriverClassName,
      String valorisationJdbcUrl, String valorisationUsername, String valorisationPassword) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(valorisationDriverClassName);
    dataSource.setUrl(valorisationJdbcUrl);
    dataSource.setUsername(valorisationUsername);
    dataSource.setPassword(valorisationPassword);
    return dataSource;
  }

}
