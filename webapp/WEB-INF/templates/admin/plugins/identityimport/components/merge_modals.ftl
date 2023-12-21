<#macro mergeModals identity_workflow identityToKeep>
	<div class="modal fade" id="merge-modal" tabindex="-1" aria-labelledby="mergeModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg rounded-5">
			<div class="modal-content rounded-5">
				<form id="mediation-merge-form" class="form-inline container" action="jsp/admin/plugins/identityimport/ManageBatchs.jsp">
					<#assign action=identity_workflow.actions?filter(a -> a.id == 6)?first!{}>
					<input type="hidden" name="actionId" value="${action.id}" />
					<input type="hidden" name="id" value="${identity_workflow.resource.id}" />
					<input type="hidden" name="customer_id" value="${identityToKeep.customerId!''}" />
					<input type="hidden" name="last_update_date" value="${identityToKeep.lastUpdateDate?string['yyyy-MM-dd HH:mm:ss.SSS']}" />
					<div class="modal-header border-0">
						<h1 class="modal-title text-center w-100 p-4 pb-0" id="mergeModalLabel">#i18n{identityimport.complete_identity.confirm}</h1>
						<button type="button" class="btn btn-rounded border position-absolute end-0 me-3 top-0 mt-3" data-bs-dismiss="modal" aria-label="Close">x</button>
					</div>
					<div class="modal-body text-center border-0 pt-0">
						<h3 class="text-center w-100">#i18n{identityimport.complete_identity.confirmMerge}</h3>
						<ul class="text-start">
							<li><b>#i18n{identityimport.complete_identity.select.message}</b></li>
							<li class="d-none">
								<b>#i18n{identityimport.complete_identity.copyAttributes}</b>
								<ul id="recap-attributes-merge-ul"></ul>
							</li>
						</ul>
					</div>
					<div class="modal-footer justify-content-center pb-4 pt-0 border-0">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">#i18n{identityimport.complete_identity.buttonCancel}</button>
						<button type="submit" form="mediation-merge-form" class="btn btn-primary" name="action_processIdentityAction">#i18n{identityimport.complete_identity.button.confirm}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal fade" id="notify-modal" tabindex="-1" aria-labelledby="notifyModalLabel" aria-hidden="true">
		<div class="modal-dialog rounded-5">
			<div class="modal-content rounded-5">
				<form class="form-inline container" action="jsp/admin/plugins/identityimport/ManageBatchs.jsp">
					<div class="modal-header border-0">
						<h1 class="modal-title text-center w-100 p-4 pb-0" id="notifyModalLabel">#i18n{identityimport.complete_identity.confirm}</h1>
						<button type="button" class="btn btn-rounded border position-absolute end-0 me-3 top-0 mt-3" data-bs-dismiss="modal" aria-label="Close">x</button>
					</div>
					<div class="modal-body text-center border-0 pt-0">
						Fonctionnalit&eacute; non d&eacute;velopp&eacute;e.
					</div>
					<div class="modal-footer justify-content-center pb-4 pt-0 border-0">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">#i18n{identityimport.complete_identity.buttonCancel}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</#macro>