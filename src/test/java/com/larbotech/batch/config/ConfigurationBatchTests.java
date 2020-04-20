package com.larbotech.batch.config;

import static com.larbotech.batch.util.TestHelper.creerCourbeDeCharge;

import com.larbotech.batch.exception.ServiceEntrepotException;
import com.larbotech.batch.job.ConfigurationJobDonneesAggregeesJournalier;
import com.larbotech.batch.modele.DonneesCanalValorisables;
import com.larbotech.batch.modele.DonneesCanalValorisees;
import com.larbotech.batch.service.ServiceDonneesEntrepot;
import com.larbotech.batch.service.ServiceDonneesValorisation;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(ConfigurationJobDonneesAggregeesJournalier.class)
@ComponentScan(basePackages = {"netseenergy.entrepot"})
@TestPropertySource("classpath:context-test-batch.properties")
@EnableTransactionManagement
@ImportResource("classpath:test-persistence-context.xml")
public class ConfigurationBatchTests {

  public static final long ID_CANAL_11_TRIGGER_EXCEPTION = 11L;
  public static final long ID_CANAL_42_SKIP_LIMIT_EXCEPTION = 42L;
  public static final long ID_CANAL_43_SKIP_LIMIT_EXCEPTION = 43L;
  public static final List<Long> ID_CANAUX_SKIP_LIMIT_EXCEPTION = Lists.newArrayList(ID_CANAL_42_SKIP_LIMIT_EXCEPTION, ID_CANAL_43_SKIP_LIMIT_EXCEPTION);

  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  JobRepository jobRepository;

  @Value("${valorisation.service.url}")
  private String urlValorisation;

  @Autowired
  @Qualifier("restTemplateValorisation")
  private RestTemplate restTemplate;

  @Bean
  public JobLauncherTestUtils jobLauncherTestUtils() {
    JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    jobLauncherTestUtils.setJobLauncher(jobLauncher);
    jobLauncherTestUtils.setJobRepository(jobRepository);

    return jobLauncherTestUtils;
  }

  @Bean
  @Primary
  public ServiceDonneesEntrepot getServiceDonneesEntrepotTest() {
    return donneeCanauxParam -> {
      if (donneeCanauxParam.getIdsCanaux().stream().findFirst().get().equals(10L)) {
        return Optional.empty();
      }
      if (ID_CANAUX_SKIP_LIMIT_EXCEPTION.contains(donneeCanauxParam.getIdsCanaux().stream().findFirst().get())) {
        throw new ServiceEntrepotException("test");
      }
      return Optional.of(creerCourbeDeCharge(1L));
    };
  }

  @Bean
  @Primary
  public ServiceDonneesValorisation getServiceDonneesValorisationTest() {
    return (DonneesCanalValorisables donneesCanalValorisables) -> {
      if (donneesCanalValorisables.getIdCanal().equals(ID_CANAL_11_TRIGGER_EXCEPTION)) {
        throw new RestClientException("Test exception");
      }

      List<DonneesCanalValorisables> listeDonneesCanalValorisables = Lists
          .newArrayList(donneesCanalValorisables);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<List<DonneesCanalValorisables>> requestEntity =
          new HttpEntity<>(listeDonneesCanalValorisables, headers);

      List<DonneesCanalValorisees> donneesCanalValorisees = restTemplate.exchange(
          urlValorisation + "/facturation/recuperation-facturations-consommation-journaliere.json",
          HttpMethod.POST,
          requestEntity,
          new ParameterizedTypeReference<List<DonneesCanalValorisees>>() {
          }).getBody();

      return donneesCanalValorisees.stream().findFirst();
    };
  }
}
