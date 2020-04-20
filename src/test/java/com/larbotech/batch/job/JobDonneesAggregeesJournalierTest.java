package com.larbotech.batch.job;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.larbotech.batch.JobExample.TIMESTAMP_PARAM_NAME;
import static com.larbotech.batch.util.TestHelper.creerCanal;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.larbotech.batch.log.LogBatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import netseenergy.entrepot.metier.canal.Canal;
import netseenergy.entrepot.metier.canal.ProprietePhysique;
import netseenergy.entrepot.metier.canal.TypeDonnees;
import netseenergy.entrepot.persistence.dao.DaoCanal;
import netseenergy.entrepot.persistence.dao.DaoProprietePhysique;
import netseenergy.entrepot.persistence.dao.DaoTypeDonnees;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class JobDonneesAggregeesJournalierTest extends AbstractJobTests {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private DaoTypeDonnees daoTypeDonnees;

  @Autowired
  private DaoProprietePhysique daoProprietePhysique;

  @Autowired
  private DaoCanal daoCanal;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().port(8089), false);

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Before
  public void before() {
    LogBatch.setDefaultMDCValue();

    MockitoAnnotations.initMocks(this);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    ProprietePhysique proprietePhysique = daoProprietePhysique.save(new ProprietePhysique(6L));
    TypeDonnees typeDonnees = daoTypeDonnees.save(new TypeDonnees(1L));

    Canal canal1 = creerCanal(proprietePhysique, typeDonnees, org.joda.time.LocalDateTime.now());
    Canal canal2 = creerCanal(proprietePhysique, typeDonnees, org.joda.time.LocalDateTime.now());

    daoCanal.save(Arrays.asList(canal1, canal2));
  }

  @Test
  public void jobBatchAgregat() throws Exception {
    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(
            new JobParametersBuilder().addDate(TIMESTAMP_PARAM_NAME, new Date()).toJobParameters());

    //THEN
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    List<StepExecution> stepExecutions = new ArrayList<>(jobExecution.getStepExecutions());

    //step init, step master et step partition0
    assertThat(stepExecutions).hasSize(3);

    StepExecution step1 = stepExecutions.get(0);
    StepExecution step2 = stepExecutions.get(1);
    StepExecution step3 = stepExecutions.get(2);

    assertThat(step1.getSummary()).contains("readCount=2", "writeCount=2");
    assertThat(step2.getSummary()).contains("readCount=2", "writeCount=2");
    assertThat(step3.getSummary()).contains("readCount=2", "writeCount=2");
  }
}
