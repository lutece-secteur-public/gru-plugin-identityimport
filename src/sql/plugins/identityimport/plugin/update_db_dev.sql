-- #54 [Import] Import d'un fichier CSV manuel: ajout d'une colonne reference dans les batchs
ALTER TABLE identityimport_batch DROP COLUMN reference;
ALTER TABLE identityimport_batch ADD COLUMN reference VARCHAR(255);
ALTER TABLE identityimport_batch ADD UNIQUE (reference);
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
ALTER TABLE identityimport_client ADD UNIQUE (token);
ALTER TABLE identityimport_client ADD UNIQUE (app_code);