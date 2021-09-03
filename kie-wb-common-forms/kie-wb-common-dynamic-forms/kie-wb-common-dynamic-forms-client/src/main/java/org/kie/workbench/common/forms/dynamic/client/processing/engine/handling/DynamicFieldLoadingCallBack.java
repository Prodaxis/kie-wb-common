package org.kie.workbench.common.forms.dynamic.client.processing.engine.handling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.common.rendering.client.widgets.util.DefaultValueListBoxRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

public class DynamicFieldLoadingCallBack <TYPE, OPTION extends SelectorOption<TYPE>> implements RemoteCallback, ErrorCallback {

	FormField field;
	FormHandler formHandler;
	
	public DynamicFieldLoadingCallBack(FormField field, FormHandler formHandler) {
		this.field = field;
		this.formHandler = formHandler;
	}

	@Override
	public boolean error(Object message, Throwable throwable) {
		return false;
	}

	@Override
	public void callback(Object response) {
		try {
			FieldDefinition fieldDefinition = (FieldDefinition) response;
			FieldType fieldType = fieldDefinition.getFieldType();
			Map<String, Object> model = (Map<String, Object>) formHandler.getModel();
			if(TextBoxFieldType.NAME.equals(fieldType.getTypeName())){
				if(fieldDefinition.getDataInitialLoaded() != null && fieldDefinition.isDoLoadInitialData()){
					setFieldValue(field, fieldDefinition.getDataInitialLoaded());
					model.put(field.getFieldBinding(), fieldDefinition.getDataInitialLoaded());
				}
			}else if(ListBoxFieldType.NAME.equals(fieldType.getTypeName()) || "ComboBox".equals(fieldType.getTypeName())){
				SelectorFieldBaseDefinition selectorFieldBaseDefinition = (SelectorFieldBaseDefinition) fieldDefinition;
				List<OPTION> selectorOptionList = selectorFieldBaseDefinition.getOptions();
				Map<TYPE, String> optionsValues = new HashMap<>();
				for (OPTION option : selectorOptionList) {
		            optionsValues.put(option.getValue(), option.getText());
		        }
				DefaultFormGroup formGroup = (DefaultFormGroup) field.getContainer();
				ValueListBox<TYPE> originwidgetList = (ValueListBox<TYPE>) formGroup.getBindableWidget().asWidget();
				DefaultValueListBoxRenderer<TYPE> optionsRenderer = new DefaultValueListBoxRenderer<>();
				optionsRenderer.setValues(optionsValues);
				List<TYPE> values = optionsValues.keySet().stream().collect(Collectors.toList());
		        ValueListBox<TYPE> widgetList = new ValueListBox<TYPE>(optionsRenderer);
		        widgetList.setId(originwidgetList.getId());
		        widgetList.setName(originwidgetList.getName());
		        widgetList.setAcceptableValues(values);
				formGroup.render(widgetList, fieldDefinition);
				model.put(field.getFieldBinding(), null);
				widgetList.addValueChangeHandler(new ValueChangeHandler<TYPE>() {
					@Override
					public void onValueChange(ValueChangeEvent<TYPE> event) {
						String value = (String) event.getValue();
						model.put(field.getFieldBinding(), value);
					}
				});
			}
		} catch (Exception e) {
			throw new IllegalStateException("Can not load data dynamic");
		}
	}
	
	private void setFieldValue(FormField field, Object value) {
    	if (field.getWidget() instanceof HasValue) {
            ((HasValue) field.getWidget()).setValue(value);;
        } else if (field.getWidget() instanceof TakesValue) {
            ((TakesValue) field.getWidget()).setValue(value);
        } else if (field.getWidget() instanceof HasText) {
            ((HasText) field.getWidget()).setText(value == null ? "" : value.toString());
        }else{
        	throw new IllegalStateException("Unexpected widget type: impossible to set the value");
        }
    }

}
