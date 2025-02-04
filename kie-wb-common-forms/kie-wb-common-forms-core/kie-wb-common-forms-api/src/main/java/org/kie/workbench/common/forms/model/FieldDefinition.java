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
package org.kie.workbench.common.forms.model;

import java.util.List;

public interface FieldDefinition {

    String getId();

    void setId(String id);

    FieldType getFieldType();

    String getName();

    void setName(String name);

    String getLabel();

    void setLabel(String label);
    
    void setPlaceHolder(String placeHolder);

    Boolean getRequired();

    void setRequired(Boolean required);

    Boolean getReadOnly();

    void setReadOnly(Boolean readOnly);

    String getHelpMessage();

    void setHelpMessage(String helpMessage);

    String getBinding();

    void setBinding(String binding);

    String getStandaloneClassName();

    void setStandaloneClassName(String standaloneClassName);

    TypeInfo getFieldTypeInfo();

    Boolean getValidateOnChange();

    void setValidateOnChange(Boolean validateOnChange);

    void copyFrom(FieldDefinition other);
    
    // <Prodaxis>
    String getMethodClassMappingParteor();
    
    String getKeyMappingParteor();
    
    void setKeyMappingParteor(String keyMappingParteor);
    
    String getValueMappingParteor();
    
    void setValueMappingParteor(String valueMappingParteor);
    
    Boolean isCheckValueExist();

    Boolean isDoLoadInitialData();
    
    void setDoLoadInitialData(Boolean doLoadInitialData);
    
    Object getDataInitialLoaded();
    
    void setDataInitialLoaded(Object data);
    
    Boolean isAutocompletedFromDataSource();
    
    void setCheckExistValue(Boolean checkExistValue);
    
    void setAutocompletedFromDataSource(Boolean autocompletedFromDataSource);
    
    String getAsyncErrorKey();
    
    void setAsyncErrorKey(String asyncErrorKey);
    
    List getSelectorOptions();

    void setSelectorOptions(List options);
    
    List getListOfValuesSelector();

    void setListOfValuesSelector(List listOfValues);
    
    Boolean getAllowMultiSelection();

	void setAllowMultiSelection(Boolean allowMultiSelection);
    // </Prodaxis>
}
