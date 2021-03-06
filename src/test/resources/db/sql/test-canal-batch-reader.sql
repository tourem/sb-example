CREATE TABLE CANAL_BATCH
(
  ID_CANAL                     NUMBER       NOT NULL,
  DATE_DEBUT_MODIF_DONNEES_UTC DATE         NOT NULL,
  DATE_FIN_MODIF_DONNEES_UTC   DATE         NOT NULL,
  CODE_RESSOURCE               VARCHAR2(30) NOT NULL,
  CONSTRAINT PK_CANAL_BATCH
  PRIMARY KEY (ID_CANAL, DATE_DEBUT_MODIF_DONNEES_UTC, DATE_FIN_MODIF_DONNEES_UTC)
)
