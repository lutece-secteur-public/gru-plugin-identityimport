
--
-- Structure for table identityimport_batch
--

DROP TABLE IF EXISTS identityimport_batch;
CREATE TABLE identityimport_batch (
id_batch int AUTO_INCREMENT,
date date NOT NULL,
user varchar(255) default '' NOT NULL,
app_code varchar(50) default '' NOT NULL,
comment long varchar,
PRIMARY KEY (id_batch)
);

--
-- Structure for table identityimport_candidate_identity
--

DROP TABLE IF EXISTS identityimport_candidate_identity;
CREATE TABLE identityimport_candidate_identity (
id_candidate_identity int AUTO_INCREMENT,
id_batch int default '0' NOT NULL,
connection_id varchar(255) default '',
customer_id varchar(255) default '',
client_id varchar(255) default '',
status VARCHAR(255) ,
PRIMARY KEY (id_candidate_identity)
);

ALTER TABLE identityimport_candidate_identity 
ADD INDEX IDX_BATCH_CANDIDATE_IDENTITIES (id_batch) ;
--
-- Structure for table identityimport_candidate_identity_attribute
--

DROP TABLE IF EXISTS identityimport_candidate_identity_attribute;
CREATE TABLE identityimport_candidate_identity_attribute (
id_candidate_identity_attribute int AUTO_INCREMENT,
id_candidate_identity int ,
code varchar(255) default '' NOT NULL,
value long varchar,
cert_process varchar(255) default '',
cert_date date,
PRIMARY KEY (id_candidate_identity_attribute)
);

ALTER TABLE identityimport_candidate_identity_attribute 
ADD INDEX IDX_CANDIDATE_IDENTITIY_ATTRIBUTES_PARENT (id_candidate_identity) ;

--
-- Structure for table identityimport_client
--

DROP TABLE IF EXISTS identityimport_client;
CREATE TABLE identityimport_client (
id_client int AUTO_INCREMENT,
name varchar(255) default '' NOT NULL,
app_code varchar(50) default '' NOT NULL,
token long varchar,
PRIMARY KEY (id_client)
);
