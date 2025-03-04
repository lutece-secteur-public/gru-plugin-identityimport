-- #54 [Import] Import d'un fichier CSV manuel: ajout d'une colonne reference dans les batchs
ALTER TABLE identityimport_batch DROP COLUMN reference;
ALTER TABLE identityimport_batch ADD COLUMN reference VARCHAR(255);
ALTER TABLE identityimport_batch ADD UNIQUE (reference);
ALTER TABLE identityimport_batch ADD client_code varchar(50) default '' NOT NULL;
UPDATE identityimport_batch SET reference = CONCAT('REF-', id_batch) WHERE reference IS NULL;
ALTER TABLE identityimport_batch MODIFY COLUMN reference VARCHAR(255) NOT NULL;

-- Add history extension fot candidate identity resources
ALTER TABLE identityimport_candidate_identity DROP COLUMN status;
DROP TABLE IF EXISTS identityimport_candidate_identity_history;
CREATE TABLE identityimport_candidate_identity_history(
id_history int AUTO_INCREMENT,
id_wf_resource_history int,
status VARCHAR(255) NOT NULL,
comment VARCHAR(1024),
PRIMARY KEY (id_history)
);

-- #80 - [ Import ] Supression automatique des lots traités
ALTER TABLE identityimport_client ADD COLUMN data_retention_period_in_months INT DEFAULT 0 NOT NULL;
ALTER TABLE identityimport_client MODIFY token VARCHAR(256);
ALTER TABLE identityimport_client ADD UNIQUE (token);
ALTER TABLE identityimport_client ADD UNIQUE (app_code);
ALTER TABLE identityimport_client ADD client_code varchar(50) default '' NOT NULL;
ALTER TABLE identityimport_client ADD UNIQUE (client_code);
ALTER TABLE identityimport_client DROP CONSTRAINT app_code;

-- #29416 - Ajout de l'heure dans la date du batch
CREATE TABLE tmp_batch_date (
    id_batch int,
    date date
);
INSERT INTO tmp_batch_date (SELECT b.id_batch, b.date FROM identityimport_batch b);
ALTER TABLE identityimport_batch DROP COLUMN date;
ALTER TABLE identityimport_batch ADD COLUMN date_create TIMESTAMP(3);
UPDATE identityimport_batch b, tmp_batch_date tbd
SET b.date_create = TIMESTAMP(DATE_FORMAT(tbd.date, '%Y-%m-%d'), '00:00:00.000')
WHERE b.id_batch = tbd.id_batch;
ALTER TABLE identityimport_batch MODIFY COLUMN date_create TIMESTAMP(3) NOT NULL;
DROP TABLE tmp_batch_date;

-- #29416 - Ajout de l'heure dans la date de certification des attributs
CREATE TABLE tmp_cert_date (
   id_candidate_identity_attribute int,
   cert_date date
);
INSERT INTO tmp_cert_date (SELECT b.id_candidate_identity_attribute, b.cert_date FROM identityimport_candidate_identity_attribute b);
ALTER TABLE identityimport_candidate_identity_attribute DROP COLUMN cert_date;
ALTER TABLE identityimport_candidate_identity_attribute ADD COLUMN cert_date TIMESTAMP(3);
UPDATE identityimport_candidate_identity_attribute b, tmp_cert_date tbd
SET b.cert_date = TIMESTAMP(DATE_FORMAT(tbd.cert_date, '%Y-%m-%d'), '00:00:00.000')
WHERE b.id_candidate_identity_attribute = tbd.id_candidate_identity_attribute;
ALTER TABLE identityimport_candidate_identity_attribute MODIFY COLUMN cert_date TIMESTAMP(3) NOT NULL;
DROP TABLE tmp_cert_date;