-- #54 [Import] Import d'un fichier CSV manuel: ajout d'une colonne reference dans les batchs
ALTER TABLE identityimport_batch DROP COLUMN reference;
ALTER TABLE identityimport_batch ADD COLUMN reference VARCHAR(255);
ALTER TABLE identityimport_batch ADD UNIQUE (reference);
UPDATE identityimport_batch SET reference = CONCAT('REF-', id_batch) WHERE reference IS NULL;
ALTER TABLE identityimport_batch MODIFY COLUMN reference VARCHAR(255) NOT NULL;