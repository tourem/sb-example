CREATE TABLE IF NOT EXISTS DONNEES_AGREGAT_JOUR
(
  ID_CANAL       INTEGER NOT NULL,
  DATE_DONNEE    DATE   NOT NULL,
  ID_SITE        INTEGER NOT NULL,
  CODE_RESSOURCE VARCHAR(32),
  VALEUR_CONSO   INTEGER,
  VALEUR_VALO    INTEGER,
  DATE_INS       DATE,
  USER_INS       VARCHAR(100),
  DATE_UPD       DATE,
  USER_UPD       VARCHAR(100),
  CONSTRAINT PK_DONNEES_AGREGAT
  PRIMARY KEY (ID_CANAL, DATE_DONNEE)
);