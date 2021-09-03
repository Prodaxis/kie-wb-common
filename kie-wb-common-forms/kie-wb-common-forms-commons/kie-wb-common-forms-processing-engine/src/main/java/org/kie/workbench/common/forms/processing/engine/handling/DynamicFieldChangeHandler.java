package org.kie.workbench.common.forms.processing.engine.handling;

import java.util.HashMap;

public interface DynamicFieldChangeHandler {
	void onFieldChange(FormHandler formHandler, FormField field, HashMap formData);
}
