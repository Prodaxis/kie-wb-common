/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.ValidableFormGroup;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FieldContainer;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;

public abstract class FormFieldImpl<F extends FieldDefinition> implements FormField {

    protected F field;

    protected FormGroup formGroup;

    private List<CustomFieldValidator> customValidators = new ArrayList<>();

    public FormFieldImpl(F field,
                         FormGroup formGroup) {
        Assert.notNull("Field cannot be null",
                       field);
        Assert.notNull("FormGroup cannot be null",
                       formGroup);
        this.field = field;
        this.formGroup = formGroup;
    }

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public String getFieldBinding() {
        return field.getBinding();
    }

    @Override
    public String getMethodClassMappingParteor() {
    	return field.getMethodClassMappingParteor();
    }
    
    @Override
    public String getKeyMappingParteor() {
        return field.getKeyMappingParteor();
    }
    
    @Override
    public boolean isValidateOnChange() {
        return field.getValidateOnChange();
    }

    @Override
    public boolean isBindable() {
        return field.getBinding() != null && !field.getBinding().isEmpty();
    }

    @Override
    public void setVisible(boolean visible) {
        formGroup.setVisible(visible);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (!field.getReadOnly()) {
            doSetReadOnly(readOnly);
        }
    }

    @Override
    public void setLabel(String label){
        field.setLabel(label);
    }
    
    @Override
    public void setPlaceHolder(String placeHolder){
    	field.setPlaceHolder(placeHolder);
    }
    
    @Override
    public FieldContainer getContainer() {
        return formGroup;
    }

    @Override
    public boolean isRequired() {
        return field.getRequired();
    }
    
    @Override
    public boolean isCheckValueExist() {
        return field.isCheckValueExist();
    }
    
    @Override
    public boolean isDoLoadInitialData(){
        return field.isDoLoadInitialData();
    }

    @Override
    public boolean isAutocompletedFromDataSource(){
        return field.isAutocompletedFromDataSource();
    }
    
    public List getSelectorOptions(){
    	return field.getSelectorOptions();
    }

    public void setSelectorOptions(List options){
    	field.setSelectorOptions(options);
    }
    
    public List getListOfValuesSelector(){
    	return field.getListOfValuesSelector();
    }

    public void setListOfValuesSelector(List listOfValues){
    	field.setListOfValuesSelector(listOfValues);
    }
    
    protected abstract void doSetReadOnly(boolean readOnly);

    public String getAsyncErrorKey() {
        return field.getAsyncErrorKey();
    }
    
    public void setAsyncErrorKey(String asyncErrorKey) {
        field.setAsyncErrorKey(asyncErrorKey);
    }
    
    @Override
    public void clearError() {
        if (formGroup instanceof ValidableFormGroup) {
            ((ValidableFormGroup) formGroup).clearError();
        }
    }

    @Override
    public void showError(String error) {
        if (formGroup instanceof ValidableFormGroup) {
            ((ValidableFormGroup) formGroup).showError(error);
        }
    }

    @Override
    public void showWarning(String warning) {
        if (formGroup instanceof ValidableFormGroup) {
            ((ValidableFormGroup) formGroup).showWarning(warning);
        }
    }
    
    @Override
    public void showSuccess(String success) {
        if (formGroup instanceof ValidableFormGroup) {
            ((ValidableFormGroup) formGroup).showSuccess(success);
        }
    }

    @Override
    public IsWidget getWidget() {
        return formGroup.getBindableWidget();
    }

    @Override
    public List<CustomFieldValidator> getCustomValidators() {
        return customValidators;
    }

	@Override
	public Object getFieldDefinition() {
		return field;
	}
    
}
