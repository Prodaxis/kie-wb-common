package org.kie.workbench.common.forms.dynamic.client.processing.engine.handling;

import java.util.HashMap;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.forms.dynamic.service.shared.ParteorComponentDataService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.DynamicFieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;

@Dependent
public class DynamicFieldChangeHandlerImpl implements DynamicFieldChangeHandler {

	@Inject
    protected Caller<ParteorComponentDataService> parteorComponentDataService;
	
	@Override
	public void onFieldChange(FormHandler formHandler, FormField field, HashMap formData) {
			DynamicFieldLoadingCallBack callBack = new DynamicFieldLoadingCallBack<>(field, formHandler);
			parteorComponentDataService.call(callBack, callBack).loadDynamicParteorField((FieldDefinition) field.getFieldDefinition(), formData);
	}
}
