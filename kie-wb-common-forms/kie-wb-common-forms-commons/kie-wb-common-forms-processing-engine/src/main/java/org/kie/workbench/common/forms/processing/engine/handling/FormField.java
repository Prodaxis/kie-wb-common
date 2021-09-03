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

package org.kie.workbench.common.forms.processing.engine.handling;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface FormField {

    String getFieldName();

    String getFieldBinding();
    
    String getMethodClassMappingParteor();
    
    String getKeyMappingParteor();

    boolean isValidateOnChange();

    boolean isBindable();

    void setVisible(boolean visible);

    void setReadOnly(boolean readOnly);

    boolean isRequired();
    
    boolean isCheckValueExist();
    
    public List getSelectorOptions();

    public void setSelectorOptions(List options);
    
    public List getListOfValuesSelector();

    public void setListOfValuesSelector(List listOfValues);
    
    boolean isDoLoadInitialData();
    
    boolean isAutocompletedFromDataSource();

    void clearError();

    void showError(String error);

    void showWarning(String warning);
    
    void showSuccess(String success);
    
    String getAsyncErrorKey();
    
    void setAsyncErrorKey(String asyncErrorKey);
    
    void setLabel(String label);
    
    void setPlaceHolder(String placeHolder);

    FieldContainer getContainer();

    IsWidget getWidget();

    default boolean isContentValid() {
        return true;
    }

    Collection<FieldChangeListener> getChangeListeners();

    Collection<CustomFieldValidator> getCustomValidators();
    
    Object getFieldDefinition();
}
