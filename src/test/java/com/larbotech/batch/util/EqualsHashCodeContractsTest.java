package com.larbotech.batch.util;

import com.larbotech.batch.metier.DonneesAgregatJour;
import com.larbotech.batch.modele.DonneesCanalBatch;
import com.larbotech.batch.modele.DonneesCanalValorisables;
import com.larbotech.batch.modele.DonneesCanalValorisees;
import com.larbotech.batch.modele.InfosCanal;
import com.larbotech.batch.modele.InfosRessource;
import com.larbotech.batch.metier.CanalBatch;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class EqualsHashCodeContractsTest {

  @Test
  public void checkCanalBatch() {
    EqualsVerifier.forClass(CanalBatch.class).verify();
  }

  @Test
  public void checkDonneesCanalBatch() {
    EqualsVerifier.forClass(DonneesCanalBatch.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void checkDonneesAgregatJour() {
    EqualsVerifier.forClass(DonneesAgregatJour.class)
        .withOnlyTheseFields("idCanal", "dateDonnee")
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void checkInfosCanal() {
    EqualsVerifier.forClass(InfosCanal.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void checkInfosRessource() {
    EqualsVerifier.forClass(InfosRessource.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void checkDonneesCanalValorisables() {
    EqualsVerifier.forClass(DonneesCanalValorisables.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void checkDonneesCanalValorisees() {
    EqualsVerifier.forClass(DonneesCanalValorisees.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }
}