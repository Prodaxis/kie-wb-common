/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.Collection;
import java.util.Map;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

public class LoadParteorFieldConfigCallback implements RemoteCallback, ErrorCallback {

    FormHandler formHandler;
    
    TranslationService translationService;

    public LoadParteorFieldConfigCallback(FormHandler formHandler, TranslationService translationService) {
        this.formHandler = formHandler;
        this.translationService = translationService;
    }

    @Override
    public boolean error(Object message, Throwable throwable) {
        return false;
    }

    @Override
    public void callback(Object response) {
        if(response instanceof Map){
            Map resultMap = (Map) response;
            Object methodValid = resultMap.get("METHOD_VALID");
            Object fieldObjects = resultMap.get("FIELD_OBJECT");
            Object fieldLabel = resultMap.get("FIELD_LABEL");
            FormField methodClassMappingParteorField = null, keyMappingParteorField = null, checkExistValueField = null, optionsField = null, listOfValuesField = null, doLoadInitialDataField = null, autocompletedFromDataSourceField = null; 
            Collection<FormField> fields = formHandler.getForm().getFields();
            for (FormField formField : fields) {
                String fieldBinding = formField.getFieldBinding();
                if(null != fieldBinding){
                    if(null != fieldLabel){
                    	if("label".equals(fieldBinding)){
                    		formField.showSuccess(translationService.format(FormEditorConstants.LoadLabelSuccess, fieldLabel));
                        }
                        else if("placeHolder".equals(fieldBinding)){
                        	formField.showSuccess(translationService.format(FormEditorConstants.ReplaceLabelOrPlaceHolderNotEmpty, fieldLabel));
                        }
                    }
                    
                    if("options".equals(fieldBinding)){
                    	optionsField = formField;
                    }else if("methodClassMappingParteor".equals(fieldBinding)){
                    	methodClassMappingParteorField = formField;
                    }else if(fieldBinding.equals("keyMappingParteor")){
                    	keyMappingParteorField = formField;
                    }else if("checkExistValue".equals(fieldBinding)){
                    	checkExistValueField = formField;
                    }
                    else if("listOfValues".equals(fieldBinding)){
                    	listOfValuesField = formField;
                    }
                    else if("doLoadInitialData".equals(fieldBinding)){
                    	doLoadInitialDataField = formField;
                    }
                    else if("autocompletedFromDataSource".equals(fieldBinding)){
                    	autocompletedFromDataSourceField = formField;
                    }
                }
            }
            
            Object methodClassMappingValue = null;
            if(null != methodClassMappingParteorField && null != methodValid){
            	methodClassMappingValue = getFieldValue(methodClassMappingParteorField);
            	if(null != methodClassMappingValue && !"".equals(methodClassMappingValue)){
            		// valid Method Class Mapping
            		if(methodValid.equals(Boolean.TRUE)){
            			methodClassMappingParteorField.showSuccess(translationService.format(FormEditorConstants.CheckMethodSuccess));
            		}else{
            			methodClassMappingParteorField.showError(translationService.format(FormEditorConstants.CheckMethodFailed));
            		}
            		// valid Load Initial Data
            		if(null != doLoadInitialDataField && getFieldValue(doLoadInitialDataField).equals(Boolean.FALSE)){
            			doLoadInitialDataField.showWarning(translationService.format(FormEditorConstants.ListOverright, translationService.format(FormRenderingConstants.FieldPropertiesMethodClassMappingParteor), translationService.format(FormRenderingConstants.FieldPropertiesDoLoadInitialData)));
            		}
            	}
            }
            
            if(null != keyMappingParteorField && null != fieldObjects){
            	Object keyMappingValue = getFieldValue(keyMappingParteorField);
            	if(null != keyMappingValue && !"".equals(keyMappingValue)){
            		if(null == methodClassMappingValue || "".equals(methodClassMappingValue)){ // if methodClassMapping is not remplir
            			if(null != checkExistValueField){
            				if(null != autocompletedFromDataSourceField){
            					if((getFieldValue(checkExistValueField).equals(Boolean.FALSE)) && (getFieldValue(autocompletedFromDataSourceField).equals(Boolean.FALSE))){
                        			checkExistValueField.showWarning(translationService.format(FormEditorConstants.ListOverright, translationService.format(FormRenderingConstants.FieldPropertiesKeyMappingParteor), translationService.format(FormRenderingConstants.FieldPropertiesCheckExistValue)));
                        			autocompletedFromDataSourceField.showWarning(translationService.format(FormEditorConstants.ListOverright, translationService.format(FormRenderingConstants.FieldPropertiesKeyMappingParteor), translationService.format(FormRenderingConstants.FieldPropertiesAutocompletedFromDataSource)));
                    			}
            				}else{
            					if(getFieldValue(checkExistValueField).equals(Boolean.FALSE)){
            						checkExistValueField.showWarning(translationService.format(FormEditorConstants.ListOverright, translationService.format(FormRenderingConstants.FieldPropertiesKeyMappingParteor), translationService.format(FormRenderingConstants.FieldPropertiesCheckExistValue)));
            					}
            				}
            			}
                	}
            		if(null != optionsField){
                    	if((doLoadInitialDataField != null && getFieldValue(doLoadInitialDataField).equals(Boolean.TRUE))){
                            if(null != fieldObjects){
                            	doLoadInitialDataField.showSuccess(translationService.format(FormEditorConstants.BindingSuccess, fieldObjects));
                            }
                    	}
                    }
        			if(null != listOfValuesField){
                    	if((doLoadInitialDataField != null && getFieldValue(doLoadInitialDataField).equals(Boolean.TRUE))){
                            if(null != fieldObjects){
                            	doLoadInitialDataField.showSuccess(translationService.format(FormEditorConstants.BindingSuccess, fieldObjects));
                            }
                    	}
                    }
            	}
            }
        }
    }
    
    private Object getFieldValue(FormField field) {
        if (field.getWidget() instanceof HasValue) {
            return ((HasValue) field.getWidget()).getValue();
        } else if (field.getWidget() instanceof TakesValue) {
            return ((TakesValue) field.getWidget()).getValue();
        } else if (field.getWidget() instanceof HasText) {
            return ((HasText) field.getWidget()).getText();
        }
        throw new IllegalStateException("Unexpected widget type: impossible to read the value");
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
