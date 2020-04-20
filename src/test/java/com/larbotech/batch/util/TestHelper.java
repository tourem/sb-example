package com.larbotech.batch.util;

import com.larbotech.batch.enums.CodeRessource;
import com.larbotech.batch.metier.CanalSource;
import com.larbotech.batch.metier.DonneesAgregatJour;
import com.larbotech.batch.modele.DonneesCanalBatch;
import com.larbotech.batch.modele.DonneesCanalValorisables;
import com.larbotech.batch.modele.DonneesCanalValorisees;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import com.larbotech.batch.metier.CanalBatch;
import com.larbotech.batch.metier.CanalBatch.CanalBatchPK;
import netseenergy.entrepot.metier.canal.Canal;
import netseenergy.entrepot.metier.canal.ProprietePhysique;
import netseenergy.entrepot.metier.canal.TypeDonnees;
import netseenergy.entrepot.metier.donnee.CodeSpecifiqueRetour;
import netseenergy.entrepot.metier.donnee.CourbeDeCharge;
import netseenergy.entrepot.metier.donnee.DonneeCanauxParam;
import netseenergy.entrepot.metier.donnee.Resolution;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TestHelper {

  public static final LocalDate DATE_2015_10_1 = LocalDate.of(2015, 10, 1);
  public static final LocalDate DATE_2015_10_2 = LocalDate.of(2015, 10, 2);
  public static final LocalDate DATE_2015_10_13 = LocalDate.of(2015, 10, 13);
  public static final LocalDate DATE_2015_10_15 = LocalDate.of(2015, 10, 15);
  public static final LocalDate DATE_2015_10_29 = LocalDate.of(2015, 10, 29);
  public static final LocalDate DATE_2015_10_30 = LocalDate.of(2015, 10, 30);

  public static final String REQUETE_INSERT_CANAL = "INSERT INTO ETP_CANAL(ID_CANAL, DATE_DEBUT_MODIF_DONNEES_UTC, DATE_FIN_MODIF_DONNEES_UTC) VALUES (?,?,?)";
  public static final String REQUETE_COUNT_NULL_CANAL = "SELECT count(*) FROM ETP_CANAL WHERE ID_CANAL = ? AND DATE_DEBUT_MODIF_DONNEES_UTC is null AND DATE_FIN_MODIF_DONNEES_UTC is null";


  private TestHelper() {
  }

  public static DonneesCanalBatch creerMapCanalBatchListeDonneesAgregat(
      TypeDonnee typeDonnee) {
    List<DonneesAgregatJour> agregatJours = creerListeDonneesAgregatJours(typeDonnee);
    return new DonneesCanalBatch(getCanalBatch(), agregatJours);
  }

  public static List<DonneesAgregatJour> creerListeDonneesAgregatJours(
      TypeDonnee typeDonnee) {
    List<DonneesAgregatJour> agregatJours = new ArrayList<DonneesAgregatJour>();
    agregatJours
        .add(creerDonneesAgregat(LocalDate.of(2018, 1, 1), 10D, 20D, typeDonnee));
    agregatJours.add(
        creerDonneesAgregat(LocalDate.of(2018, 1, 2), 15.5D, 25.5D, typeDonnee));
    return agregatJours;
  }

  public static DonneesAgregatJour creerDonneesAgregat(long idClient, long idSite, long idCanal,
      CodeRessource ressource, LocalDate localDate, Double valeur, Double valo,
      TypeDonnee typeDonnee) {
    DonneesAgregatJour donneesAgregatJour = new DonneesAgregatJour();
    donneesAgregatJour.setIdCanal(idCanal);
    donneesAgregatJour.setDateDonnee(localDate);
    donneesAgregatJour.setCodeRessource(ressource.name());
    donneesAgregatJour.setIdSite(idSite);
    if (TypeDonnee.CONSOMMATION.equals(typeDonnee)) {
      donneesAgregatJour.setValeurConso(valeur);
      donneesAgregatJour.setDepense(true);
    }
    if (TypeDonnee.DEPENSE.equals(typeDonnee)) {
      donneesAgregatJour.setDepense(true);
      donneesAgregatJour.setValeurValo(valeur);
    } else {
      donneesAgregatJour.setValeurConso(valeur);
      donneesAgregatJour.setValeurValo(valo);
    }
    return donneesAgregatJour;
  }

  public static DonneesAgregatJour creerDonneesAgregat(LocalDate localDate, Double valeur,
      Double valo,
      TypeDonnee typeDonnee) {
    return creerDonneesAgregat(1, 1, 1, CodeRessource.ELECTRICITE, localDate, valeur, valo,
        typeDonnee);
  }

  public static CanalBatch getCanalBatch() {
    LocalDateTime datedebut = LocalDateTime.of(2018, 1, 1, 23, 0, 0);
    LocalDateTime dateFin = LocalDateTime.of(2018, 1, 2, 23, 0, 0);

    CanalBatchPK pk = new CanalBatchPK(1L, datedebut, dateFin);
    CanalBatch canalBatch = new CanalBatch();
    canalBatch.setId(pk);
    return canalBatch;
  }

  public static List<CourbeDeCharge> creerListeCourbesDeCharge() {
    return Lists.newArrayList(creerCourbeDeCharge(1L));
  }

  public static CourbeDeCharge creerCourbeDeCharge(Long idCanal) {
    CourbeDeCharge cdc = new CourbeDeCharge();
    cdc.setIdCanal(idCanal);
    SortedMap<DateTime, Double> map = new TreeMap<DateTime, Double>();
    map.put(new org.joda.time.DateTime(2018, 1, 1, 0, 0, 0)
        , 10D);
    map.put(new org.joda.time.DateTime(2018, 1, 2, 0, 0, 0)
        , 15.5D);
    cdc.setDonnees(map);
    return cdc;
  }

  public static SimpleEntry<DateTime, Double> getEntryDateTimeValeur() {
    return new SimpleEntry<>(
        convertirEnDateTimeavecTimeZone(
            new org.joda.time.LocalDateTime(2018, 1, 1, 0, 0, 0))
        , 10D);
  }


  public static DonneeCanauxParam getDonneeCanauxParam(LocalDateTime dateDebutLocalDateTime,
      LocalDateTime dateFinLocalDateTime, CodeRessource codeRessource,
      boolean estConsolidable, Resolution resolution) {
    DonneeCanauxParam param = new DonneeCanauxParam();
    org.joda.time.LocalDateTime dateDebut = new org.joda.time.LocalDateTime(
        DateHelper.convertLocalDateTimeToDate(dateDebutLocalDateTime));

    org.joda.time.LocalDateTime dateFin = new org.joda.time.LocalDateTime(
        DateHelper.convertLocalDateTimeToDate(dateFinLocalDateTime));

    param.setIdsCanaux(
        com.google.common.collect.Lists.newArrayList(1L));
    param.setDateDebutUtc(dateDebut);
    param.setDateFinUtc(dateFin);
    param.setResolution(resolution);
    param.setRetourConsolidable(estConsolidable);
    param.setTimeZoneRetour(DateHelper.TIME_ZONE_FR);

    if (CodeRessource.GAZ.equals(codeRessource) || CodeRessource.FIOUL
        .equals(codeRessource)) {
      param.setCodeSpecifiqueRetour(CodeSpecifiqueRetour.ENERGIE);
    }
    return param;
  }

  public static CanalBatch creerCanalBatch(Long idCanal, LocalDateTime dateDebut,
      LocalDateTime dateFin) {
    CanalBatch canalBatch = new CanalBatch();
    canalBatch.setId(new CanalBatch.CanalBatchPK(idCanal, dateDebut, dateFin));
    return canalBatch;
  }

  public static CanalBatch creerCanalBatch(Long idCanal) {
    LocalDateTime dateDebut = LocalDateTime.now();
    LocalDateTime dateFin = dateDebut.plusDays(1);
    return creerCanalBatch(idCanal, dateDebut, dateFin);
  }

  public static Canal creerCanal(ProprietePhysique proprietePhysique, TypeDonnees typeDonnees,
      org.joda.time.LocalDateTime date) {
    return creerCanal(proprietePhysique, typeDonnees, date, date.plusDays(1));
  }

  public static Canal creerCanal(ProprietePhysique proprietePhysique, TypeDonnees typeDonnees,
      org.joda.time.LocalDateTime dateDebut, org.joda.time.LocalDateTime dateFin) {
    Canal canal = new Canal();
    canal.setResolutionOrigine(600);
    canal.setResolutionCible(600);
    canal.setBouchageTrous(false);
    canal.setCoefficient(1.0);
    canal.setProprietePhysique(proprietePhysique);
    canal.setTypeDonnees(typeDonnees);
    canal.setDateDebutModificationDonneesUtc(dateDebut);
    canal.setDateFinModificationDonneesUtc(dateFin);
    canal.setEstProduction(true);

    return canal;
  }

  public static DonneesAgregatJour creerDonneesAgregatJour(Long idDonneesAgregatJour) {
    DonneesAgregatJour donneesAgregatJour = new DonneesAgregatJour();
    donneesAgregatJour.setIdCanal(idDonneesAgregatJour);
    donneesAgregatJour.setDateDonnee(LocalDate.now());
    donneesAgregatJour.setIdSite(2L);
    return donneesAgregatJour;
  }

  public static Optional<DonneesCanalValorisees> creerDonneesValorisees() {
    DonneesCanalValorisees valorisees = new DonneesCanalValorisees();
    SortedMap<org.joda.time.LocalDate, Double> sortedMap = new TreeMap<>();

    sortedMap.put(org.joda.time.LocalDate.parse("2018-01-01")
        , 20D);
    sortedMap.put(org.joda.time.LocalDate.parse("2018-01-02")
        , 25D);
    valorisees.setDonneesJournalieres(sortedMap);

    return Optional.of(valorisees);
  }

  private static DateTime convertirEnDateTimeavecTimeZone(
      org.joda.time.LocalDateTime localDateTime) {
    return localDateTime.toDateTime(DateTimeZone.UTC)
        .toDateTime(DateTimeZone.forID("Europe/Paris"));
  }

  public static DonneesCanalValorisables creerDonneesCanalValorisables(CodeRessource codeRessource,
      Integer resolution) {
    DonneesCanalValorisables canalValorisables = new DonneesCanalValorisables();
    canalValorisables.setIdCanal(1L);
    canalValorisables.setIdPdm(1L);
    canalValorisables.setIdPdc(1L);
    canalValorisables.setRessource(codeRessource);
    canalValorisables.setResolutionSeconde(resolution);

    SortedMap<DateTime, Double> donneesDetaillees = getCourbeDeChargeDetaillee(1).getDonnees();
    canalValorisables.setDonneesDetaillees(donneesDetaillees);

    return canalValorisables;
  }

  public static CourbeDeCharge getCourbeDeChargeDetaillee(long idCanalEntrepot) {
    SortedMap<DateTime, Double> donneesDetaillees = new TreeMap<>();
    donneesDetaillees.put(new DateTime(2018, 1, 1, 0, 0), 10.0);
    donneesDetaillees.put(new DateTime(2018, 1, 2, 0, 0), 15.5);
    return new CourbeDeCharge(idCanalEntrepot, donneesDetaillees);
  }

  public static DonneesCanalValorisees creerDonneesCanalValorisees() {
    DonneesCanalValorisees canalValorisees = new DonneesCanalValorisees();
    canalValorisees.setIdCanal(1L);

    SortedMap<org.joda.time.LocalDate, Double> donneesJournalieres = new TreeMap<>();
    donneesJournalieres.put(new DateTime(2018, 1, 1, 0, 0).toLocalDate(), 100.0);
    donneesJournalieres.put(new DateTime(2018, 1, 2, 0, 0).toLocalDate(), 80.0);
    canalValorisees.setDonneesJournalieres(donneesJournalieres);

    return canalValorisees;
  }

  public static CanalSource creerCanalSource(Long idCanal, LocalDateTime dateDebut,
      LocalDateTime dateFin) {
    CanalSource canalSource = new CanalSource();
    canalSource.setIdCanal(idCanal);
    canalSource.setDateDebutModifDonneesUTC(dateDebut);
    canalSource.setDateFinModifDonneesUTC(dateFin);
    return canalSource;
  }

  public enum TypeDonnee {
    CONSOMMATION, DEPENSE, CONSOMMATION_VALORISABLE;
  }
}