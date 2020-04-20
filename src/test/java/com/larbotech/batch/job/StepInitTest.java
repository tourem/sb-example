package com.larbotech.batch.job;

import static com.larbotech.batch.util.TestHelper.creerCanal;
import static org.assertj.core.api.Assertions.assertThat;

import com.larbotech.batch.dao.DaoDonneesAgregatJour;
import com.larbotech.batch.metier.repository.CanalBatchRepository;
import netseenergy.entrepot.metier.canal.ProprietePhysique;
import netseenergy.entrepot.metier.canal.TypeDonnees;
import netseenergy.entrepot.persistence.dao.DaoCanal;
import netseenergy.entrepot.persistence.dao.DaoProprietePhysique;
import netseenergy.entrepot.persistence.dao.DaoTypeDonnees;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"step.executable.type=INIT"})
public class StepInitTest extends AbstractJobTests {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private CanalBatchRepository canalBatchRepository;

  @Autowired
  private DaoDonneesAgregatJour daoDonneesAgregatJour;

  @Autowired
  private DaoTypeDonnees daoTypeDonnees;

  @Autowired
  private DaoProprietePhysique daoProprietePhysique;

  @Autowired
  private DaoCanal daoCanal;


  @Before
  public void before() {
    ProprietePhysique proprietePhysique = daoProprietePhysique.save(new ProprietePhysique(6L));
    TypeDonnees typeDonnees = daoTypeDonnees.save(new TypeDonnees(1L));
    daoCanal.save(creerCanal(proprietePhysique, typeDonnees, org.joda.time.LocalDateTime.now()));
  }

  @Test
  public void stepInit() throws Exception {
    //WHEN
    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(jobLauncherTestUtils.getUniqueJobParameters());

    //THEN
    assertThat(jobExecution.getStepExecutions()).hasSize(1);
    assertThat(jobExecution.getStepExecutions().stream().findFirst().get().getStepName())
        .isEqualTo(ConfigurationJobDonneesAggregeesJournalier.STEP_INIT);
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(canalBatchRepository.findAll()).hasSize(1);
    assertThat(daoDonneesAgregatJour.findAll()).isEmpty();
  }
}
