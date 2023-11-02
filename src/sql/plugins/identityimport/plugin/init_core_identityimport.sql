
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'IDENTITYIMPORT_BATCH_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('IDENTITYIMPORT_BATCH_MANAGEMENT','identityimport.adminFeature.ManageBatchs.name',1,'jsp/admin/plugins/identityimport/ManageBatchs.jsp','identityimport.adminFeature.ManageBatchs.description',0,'identityimport',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'IDENTITYIMPORT_BATCH_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('IDENTITYIMPORT_BATCH_MANAGEMENT',1);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'IDENTITYIMPORT_CANDIDATE_IDENTITY_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('IDENTITYIMPORT_CANDIDATE_IDENTITY_MANAGEMENT',1);




--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'IDENTITYIMPORT_CLIENT_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('IDENTITYIMPORT_CLIENT_MANAGEMENT','identityimport.adminFeature.ManageClients.name',1,'jsp/admin/plugins/identityimport/ManageClients.jsp','identityimport.adminFeature.ManageClients.description',0,'identityimport',NULL,NULL,NULL,5);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'IDENTITYIMPORT_CLIENT_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('IDENTITYIMPORT_CLIENT_MANAGEMENT',1);

