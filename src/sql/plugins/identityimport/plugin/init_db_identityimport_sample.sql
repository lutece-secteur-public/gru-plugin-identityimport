
REPLACE INTO workflow_workflow (id_workflow,name,description,creation_date,is_enabled,workgroup_key) VALUES (1,'Gestion des lots','Suivi et traitement des lots','2022-11-16 18:06:28',1,'all');
REPLACE INTO workflow_workflow (id_workflow,name,description,creation_date,is_enabled,workgroup_key) VALUES (2,'Import d\'une identité candidate ','Import d\'une identité candidate','2022-11-18 20:07:08',1,'all');

REPLACE INTO workflow_icon (id_icon,name,mime_type,file_value,width,height) VALUES (1,'Valider','image/png',null,14,14);
REPLACE INTO workflow_icon (id_icon,name,mime_type,file_value,width,height) VALUES (2,'Refuser','image/png',null,14,14);
REPLACE INTO workflow_icon (id_icon,name,mime_type,file_value,width,height) VALUES (3,'Commentaire','image/png',null,14,14);


REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (1,'A traiter','Etat initial',1,1,0,NULL,1);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (2,'Traité, non finalisé','Lot traité automatiquement, en attente de finalisation',1,0,0,NULL,2);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (3,'Finalisé','Lot finalisé',1,0,0,NULL,3);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (4,'A traiter','Ligne d\'identité à traiter',2,1,0,NULL,1);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (5,'Identité insérée','L\'identité a été créée',2,0,0,1,2);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (6,'Identité identifiée','L\'identité a été trouvée dans le référentiel',2,0,0,1,3);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (7,'A traiter manuellement','L\'identité est susceptible d\'exister dans le référentiel , à traiter manuellement',2,0,0,2,4);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (8,'Identité traitée automatiquement','L\'identité a pu être traitée de façon automatique (insérée ou rapprochée)',2,0,0,1,5);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (9,'Non traitée','L\'identité candidate  n\'a pu être traitée',2,0,0,2,6);
REPLACE INTO workflow_state (id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon,display_order) VALUES (10,'Refusé','Lot refusé ',1,0,0,2,4);

REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (1,'Traiter automatiquement','Lancement du traitement automatique du lot',1,2,1,0,0,1,0,-1);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (2,'Finaliser','Cloturer le lot d\'import',1,3,1,0,0,2,0,NULL);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (3,'Réinitialiser','Réinitialiser le traitement du lot',1,1,2,0,0,3,0,NULL);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (4,'Insérer','Importer la ligne',2,5,1,0,1,1,0,NULL);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (6,'Identifier une identité','Réutiliser une identité existante',2,6,1,0,0,2,0,NULL);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (11,'Importer','Action pour lancer l\'action auto d\'import',2,8,1,0,0,4,0,7);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (9,'Reinitialiser','Réinitialiser l\'état de la ligne',2,4,3,0,0,3,0,-1);
REPLACE INTO workflow_action (id_action,name,description,id_workflow,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action,id_alternative_state_after) VALUES (15,'Ne pas traiter','Ne pas traiter cette identité candidate',2,9,3,0,0,5,0,-1);


REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (1,1);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (2,2);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (3,2);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (3,3);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (4,7);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (6,7);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (9,5);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (9,6);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (9,7);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (9,8);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (9,9);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (11,4);
REPLACE INTO workflow_action_state_before (id_action,id_state_before) VALUES (15,7);




REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (6,'taskActionsBatch',1,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (7,'chooseStateTask',4,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (8,'taskTypeConfirmAction',9,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (9,'taskTypeConfirmAction',3,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (13,'taskIdentityImport',11,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (14,'taskTypeComment',15,1);
REPLACE INTO workflow_task (id_task,task_type_key,id_action,display_order) VALUES (15,'taskTypeChoice',2,1);

REPLACE INTO workflow_task_identity_import_cf (id_task,id_state1,id_state2,id_state3,id_workflow) VALUES (11,5,6,7,2);

REPLACE INTO workflow_task_confirm_action_config (id_task,message) VALUES (8,'Êtes vous sûr de vouloir réinitialiser l\'état de cette ligne ?');
REPLACE INTO workflow_task_confirm_action_config (id_task,message) VALUES (9,'Êtes vous sûr ?');

REPLACE INTO workflow_task_comment_config (id_task,title,is_mandatory,is_richtext) VALUES (14,'Indiquer la raison de ne pas importer l\'identité candidate',0,0);



REPLACE INTO workflow_task_choose_state_config (id_task,controller_name,id_state_ok,id_state_ko) VALUES (7,NULL,5,7);
REPLACE INTO workflow_task_choice_config (id_task,message) VALUES (15,'Voulez vous ?');

REPLACE INTO workflow_task_actions_batch_cf (id_task,id_workflow,id_state,id_action,resource_type) VALUES (6,2,4,11,'IDENTITYIMPORT_CANDIDATE_RESOURCE');















