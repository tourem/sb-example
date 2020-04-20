package com.larbotech.batch.job;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.larbotech.batch.config.ConfigurationBatchTests.ID_CANAL_11_TRIGGER_EXCEPTION;
import static com.larbotech.batch.util.TestHelper.creerCanalBatch;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.larbotech.batch.config.ConfigurationBatchTests;
import com.larbotech.batch.dao.DaoDonneesAgregatJour;
import com.larbotech.batch.metier.repository.CanalBatchRepository;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.larbotech.batch.metier.CanalBatch;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@TestPropertySource(properties = {"step.executable.type=AGREGAT", "step.agregat.chunk.size=2", "nombre.jour.partition=30"})
public class StepAgregatTest extends AbstractJobTests {

  public static final int NOMBRE_JOUR_PARTITION = 30;
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private CanalBatchRepository canalBatchRepository;

  @Autowired
  private DaoDonneesAgregatJour daoDonneesAgregatJour;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().port(8089), false);

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  public void stepDonneesAgregatJour()
      throws Exception {
    CanalBatch canalBatch = creerCanalBatch(1L);
    canalBatchRepository.save(canalBatch);
    canalBatch.getId().setIdCanal(2L);
    canalBatchRepository.save(canalBatch);
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);

    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(daoDonneesAgregatJour.findAll()).hasSize(4);
    assertThat(canalBatchRepository.findAll()).isEmpty();

  }

  @Test
  public void stepDonneesAgregatJour_QuandSkipLimitNePasAtteintAlorsProcessBatchContinue()
      throws Exception {
    CanalBatch canalBatch = creerCanalBatch(1L);
    canalBatchRepository.save(canalBatch);
    canalBatch.getId().setIdCanal(ConfigurationBatchTests.ID_CANAL_42_SKIP_LIMIT_EXCEPTION);
    canalBatchRepository.save(canalBatch);
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);

    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    //ne contient que les données agrégat du canal 1
    assertThat(daoDonneesAgregatJour.findAll()).hasSize(2);
    // canal 42 reste dans la canal batch
    assertThat(canalBatchRepository.findAll()).hasSize(1);

  }

  @Test
  public void stepDonneesAgregatJour_QuandSkipLimitAtteintAlorsStoppeProcessBatch()
      throws Exception {
    CanalBatch canalBatch = creerCanalBatch(ConfigurationBatchTests.ID_CANAL_43_SKIP_LIMIT_EXCEPTION);
    canalBatchRepository.save(canalBatch);
    canalBatch.getId().setIdCanal(ConfigurationBatchTests.ID_CANAL_42_SKIP_LIMIT_EXCEPTION);
    canalBatchRepository.save(canalBatch);
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);

    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    //aucun update/insert de la table agregat
    assertThat(daoDonneesAgregatJour.findAll()).hasSize(0);
    // table canal batch ne bouge pas
    assertThat(canalBatchRepository.findAll()).hasSize(2);

  }

  @Test
  public void stepDonneesAgregatJour_quand_exception_declenchee_en_premier_lot() {
    //GIVEN
    canalBatchRepository.save(creerCanalBatch(1L));
    canalBatchRepository.save(creerCanalBatch(ID_CANAL_11_TRIGGER_EXCEPTION));
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);

    //WHEN
    JobExecution jobExecution = null;
    try {
      jobExecution = jobLauncherTestUtils
          .launchJob(jobLauncherTestUtils.getUniqueJobParameters());
    } catch (Exception e) {
      assertThat(e).isInstanceOf(RestClientException.class);
    }

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);
  }

  @Test
  public void stepDonneesAgregatJour_quand_exception_declenchee_en_deuxiemme_lot()
      throws Exception {
    //GIVEN
    canalBatchRepository.save(creerCanalBatch(1L));
    canalBatchRepository.save(creerCanalBatch(2L));
    canalBatchRepository.save(creerCanalBatch(3L));
    canalBatchRepository.save(creerCanalBatch(ID_CANAL_11_TRIGGER_EXCEPTION));
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(4);

    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN : stepMaster et stepAgregat
    assertThat(jobExecution.getStepExecutions()).hasSize(2);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    //Pour chaque appel à valo, on envoie deux jours valorisation. Pour deux canaux traités, on aura 4 données agrégats jours
    assertThat(daoDonneesAgregatJour.findAll()).hasSize(4);
    //le chunk size = 2, un rollback est effectué pour le deuxième lot et reste dans CANAL_BATCH
    assertThat(canalBatchRepository.findAll()).hasSize(2);
  }

  @Test
  public void stepDonneesAgregatJour_QuandPeriodeDepasseNbJoursPartitionAlorsCreerStepSupplementaire()
      throws Exception {
    canalBatchRepository.save(creerCanalBatch(1L,
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(NOMBRE_JOUR_PARTITION - 1)));
    canalBatchRepository.save(creerCanalBatch(2L,
        LocalDateTime.now().plusDays(NOMBRE_JOUR_PARTITION + 2),
        LocalDateTime.now().plusDays(NOMBRE_JOUR_PARTITION + 5)));
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
    assertThat(canalBatchRepository.findAll()).hasSize(2);

    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(3);
    assertThat(jobExecution.getStepExecutions().stream().map(StepExecution::getStepName).collect(
        Collectors.toList())).containsExactlyInAnyOrder("stepDonneesAgregatJour:partition0","stepMaster", "stepDonneesAgregatJour:partition1");
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(daoDonneesAgregatJour.findAll()).hasSize(4);
    assertThat(canalBatchRepository.findAll()).isEmpty();

  }
}
