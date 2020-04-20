package com.larbotech.batch.config;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ConfigurationSourcesPlaceholder.class, ConfigurationDatasource.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ConfigurationBatch {

  @Value("${valorisation.service.username}")
  private String username;

  @Value("${valorisation.service.password}")
  private String password;

  @Autowired
  @Qualifier(value = "dataSourcePspe")
  private DataSource dataSourcePspe;


  @Autowired
  @Qualifier(value = "dataSource")
  private DataSource dataSourceEtp;

  @Autowired
  @Qualifier(value = "transactionManager")
  private PlatformTransactionManager platformTransactionManager;

  @Bean(name = "transactionManagerPspe")
  public PlatformTransactionManager transactionManagerPspe() {
    return new DataSourceTransactionManager(dataSourcePspe);
  }


  @Bean(name = "globalTransactionManager")
  public ChainedTransactionManager transactionManager() {
    return new ChainedTransactionManager(platformTransactionManager,
        transactionManagerPspe());
  }


  @Bean(name = "jobRepository")
  public JobRepository jobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
    factory.setDataSource(dataSourceEtp);
    factory.setTransactionManager(transactionManager());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean(name = "stepBuilderFactory")
  public StepBuilderFactory stepBuilderFactory() throws Exception {
    return new StepBuilderFactory(jobRepository(), transactionManager());
  }

  @Bean(name = "jobBuilderFactory")
  public JobBuilderFactory jobBuilderFactory() throws Exception {
    return new JobBuilderFactory(jobRepository());
  }

  @Bean
  public JobLauncher jobLauncher() throws Exception {
    SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
    simpleJobLauncher.setJobRepository(jobRepository());
    return simpleJobLauncher;
  }


  @Bean(name = "restTemplateValorisation")
  public RestTemplate restTemplateValorisation() {
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new IntercepteurBasicAuthentification(username, password));
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(interceptors);
    MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
    restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

    return restTemplate;
  }


}
