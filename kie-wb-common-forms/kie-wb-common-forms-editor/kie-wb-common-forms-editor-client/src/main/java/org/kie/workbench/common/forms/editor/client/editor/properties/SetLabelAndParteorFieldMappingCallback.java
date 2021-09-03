package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldLabelData;

public class SetLabelAndParteorFieldMappingCallback implements RemoteCallback, ErrorCallback {

	FormEditorHelper formEditorHelper;
	
	public SetLabelAndParteorFieldMappingCallback(FormEditorHelper formEditorHelper) {
		this.formEditorHelper = formEditorHelper;
	}

	@Override
	public boolean error(Object message, Throwable throwable) {
		return false;
	}

	@Override
	public void callback(Object response) {
		HashMap results = (HashMap) response;
		if(null != results){
			Iterator it = formEditorHelper.getAvailableFields().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, FieldDefinition> field = (Entry<String, FieldDefinition>) it.next();
				FieldDefinition fieldDefinition = field.getValue();
				String name = fieldDefinition.getName();
				FieldLabelData model = (FieldLabelData) results.get(name);
				if(null != model){
					fieldDefinition.setLabel(model.getFieldLabel());
					fieldDefinition.setPlaceHolder(model.getFieldLabel());
					fieldDefinition.setKeyMappingParteor(model.getFieldMapping());
				}
			}
		}
	}

}
