package org.kie.workbench.common.forms.dynamic.backend.server.shared.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.workbench.common.forms.dynamic.service.shared.ParteorComponentDataService;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.AbstractMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldLabelData;
import org.kie.workbench.common.forms.model.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prodaxis.solar.components.ComponentDescriptor;
import com.prodaxis.solar.components.ComponentManager;
import com.prodaxis.solar.components.IComponentFieldDescriptor;
import com.prodaxis.solar.services.BusinessComponentService;
import com.prodaxis.solar.udm.ModelFactory;
import com.prodaxis.solar.util.BusinessComponentScriptHelper;
import com.prodaxis.solar.util.ComponentMessageResource;
import com.prodaxis.solar.xtc.model.IObjectModel;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@Service
@ApplicationScoped
public class ParteorComponentDataServiceImpl implements ParteorComponentDataService {

	private static final Logger logger = LoggerFactory.getLogger(ParteorComponentDataServiceImpl.class);
	
	@Inject
    protected KieServerIntegration kieServerIntegration;
	
	@Override
	public boolean checkDataExist(String methodClassMappingParteor, String keyMapping, Object value) {
		 try {
	            if (null != keyMapping && !"".equals(keyMapping)) {
	                if(keyMapping.contains("#")){
	                    String[] componentClassKey = keyMapping.split("#");
	                    BusinessComponentService bcService = new BusinessComponentService();
	                    if(value instanceof Collection){
	                    	Collection valueCollection = (Collection) value;
	                    	Iterator it = valueCollection.iterator();
	                    	value = it.next();
	                    }
	                    Object data = bcService.find(componentClassKey[0], new Object[] { value });
	                    if(null != data){
	                       return true;
	                    }
	                }else{
	                    logger.error("keyMapping for value " + value + " isn't correct format like com.prodaxis...#attributOrMethod");
	                }
	            }else {
	                logger.error("keyMapping have not declare for value " + value);
	            }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return false;
	}

	@Override
	public HashMap findLiveSearch(String methodClassMappingParteor, String keyMapping, String valueMapping, String pattern, int maxResults) {
		HashMap entries = new HashMap<>();
        try {
            if (null != keyMapping && !"".equals(keyMapping)) {
                if(keyMapping.contains("#")){
                    String[] componentClassKey = keyMapping.split("#");
                    String clazz = componentClassKey[0];
                    String keyAttribut = componentClassKey[1]; 
                    BusinessComponentService bcService = new BusinessComponentService();
                    String query = "SELECT o FROM " + clazz + " o WHERE o." + keyAttribut + " LIKE :attributStartsWith";
                    Properties props = new Properties();
                    props.put("attributStartsWith", pattern + "%");
                    Object data = bcService.findMany(clazz, "findByExpression", new Object[] { query, props, maxResults});
                    if(null != data){
                        Collection results = (Collection) data;
                        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                            Object object = (Object) iterator.next();
                            IObjectModel resultModel = ModelFactory.getInstance().createObjectModel("TmpModel", object);
                            Object keyValue = resultModel.getAttribute(keyAttribut).get();
                            String text = keyValue + "";
                            if(null != valueMapping && valueMapping.contains("#")){
                                String[] valueMappings = valueMapping.split("#");
                                String valueAttribut = componentClassKey[1];
                                if(!keyAttribut.equals(valueAttribut)){
                                	Object value = resultModel.getAttribute(valueMappings[1]).get();
                                	text += ((null == value) ? "" : (" : " + value.toString()));
                                }
                            }
                            entries.put(keyValue, text);
                        }
                    }
                }else{
                    logger.error("keyMapping isn't correct format like com.prodaxis...#attributOrMethod");
                }
            }else {
                logger.error("keyMapping have not declare for autocompleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
	}

	@Override
	public Map loadParteorFieldConfig(Object methodClassMappingParteor, Object keyMapping, Object valueMapping) {
		 Map config = new HashMap();
	        try {
	        	if (null != methodClassMappingParteor && !"".equals(methodClassMappingParteor)) {
	        		boolean methodValid = BusinessComponentScriptHelper.getInstance().checkMethodExist((String) methodClassMappingParteor);
	        		config.put("METHOD_VALID", methodValid);
	        		config.put("FIELD_OBJECT", "");
	        	}
	            if (null != keyMapping && !"".equals(keyMapping)) {
	                String[] componentClassKey = keyMapping.toString().split("#");
	                String clazz = componentClassKey[0];
	                ComponentDescriptor descriptor = ComponentManager.getInstance().getComponentDescriptor(clazz);
	                if(null != descriptor){
	                	config.put("FIELD_OBJECT", descriptor.getLabel(null));
	                }else{
	                	logger.error("Cannot find component " + clazz + " in Parteor");
	                }
	                IComponentFieldDescriptor fieldDescriptor = descriptor.getField(keyMapping.toString());
	                if(null != fieldDescriptor){
	                	config.put("FIELD_LABEL", fieldDescriptor.getLabel(null));
	                }else{
	                	logger.error("Cannot find field " + keyMapping.toString() + " in class " + clazz);
	                }
	            }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return config;
	}

	@Override
	public HashMap setLabelAndParteorFieldMapping(String labelClassMapping) {
		HashMap<String, FieldLabelData> result = new HashMap<String, FieldLabelData>();
		try {
			ComponentDescriptor descriptor = ComponentManager.getInstance().getComponentDescriptor(labelClassMapping);
			if(null != descriptor){
				IComponentFieldDescriptor[] fields = descriptor.getFields();
				for (int i = 0; i < fields.length; i++) {
					IComponentFieldDescriptor fieldDescriptor = fields[i];
					FieldLabelData fieldData = new FieldLabelData(fieldDescriptor.getName(), fieldDescriptor.getLabel(null), null);
					if(fieldDescriptor.isTarget()){
						fieldData.setFieldMapping(fieldDescriptor.getTarget());
					}
					result.put(fieldDescriptor.getName(), fieldData);
				}
			}
		} catch (Exception e) {
			logger.error("Set Label And Parteor Field Mapping Error");
		}
		return result;
	}

	@Override
	public FieldDefinition loadDynamicParteorField(FieldDefinition field, HashMap formData) {
		try {
			String methodClassMapping = field.getMethodClassMappingParteor();
			field.setDataInitialLoaded(null);
			if (null != methodClassMapping && methodClassMapping.contains("#")) {
				String keyMapping = field.getKeyMappingParteor();
				String valueMapping = field.getValueMappingParteor();
				Object resultReturn = BusinessComponentScriptHelper.getInstance().executionBusinessComponentScript(null, methodClassMapping, formData);
				if (field.isDoLoadInitialData() && null != resultReturn) {
					if(null != keyMapping && keyMapping.contains("#")){ // return is collection or object
						String[] componentClassKey = keyMapping.split("#");
						String keyAttribut = componentClassKey[1];
						FieldType fieldType = field.getFieldType();
						if (null != resultReturn) {
							if (resultReturn instanceof Collection) {
								Collection results = (Collection) resultReturn;
								Iterator it = results.iterator();
								if (MultipleSelectorFieldType.NAME.equals(fieldType.getTypeName())) {
									AbstractMultipleSelectorFieldDefinition multipleSelectorField = (AbstractMultipleSelectorFieldDefinition) field;
									while (it.hasNext()) {
										IObjectModel resultModel = ModelFactory.getInstance().createObjectModel("TmpModel", it.next());
										multipleSelectorField.getListOfValues().add(resultModel.getAttribute(keyAttribut).get());
									}
								} else if (ListBoxFieldType.NAME.equals(fieldType.getTypeName()) || RadioGroupFieldType.NAME.equals(fieldType.getTypeName()) || "ComboBox".equals(fieldType.getTypeName())) {
									SelectorFieldBaseDefinition selectorFieldBaseDefinition = (SelectorFieldBaseDefinition) field;
									while (it.hasNext()) {
										IObjectModel resultModel = ModelFactory.getInstance().createObjectModel("TmpModel", it.next());
										Object keyValue = resultModel.getAttribute(keyAttribut).get();
										String text = keyValue + "";
										if (null != valueMapping && valueMapping.contains("#")) {
											String[] valueMappings = valueMapping.split("#");
											String valueAttribut = valueMappings[1];
											if (!keyAttribut.equals(valueAttribut)) {
												Object value = resultModel.getAttribute(valueAttribut).get();
												text += (null == value) ? "" : (" : " + value.toString());
											}
										}
										DefaultSelectorOption option = new DefaultSelectorOption(keyValue, text);
										selectorFieldBaseDefinition.getOptions().add(option);
									}
								}else if(TextBoxFieldType.NAME.equals(fieldType.getTypeName())){
									if(it.hasNext()){
										IObjectModel resultModel = ModelFactory.getInstance().createObjectModel("TmpModel", it.next());
										Object keyValue = resultModel.getAttribute(keyAttribut).get();
										field.setDataInitialLoaded(keyValue + "");
									}
								}
							} else if (!resultReturn.getClass().getName().startsWith("java.lang")) { // return object
								IObjectModel resultModel = ModelFactory.getInstance().createObjectModel("TmpModel", resultReturn);
								Object keyValue = resultModel.getAttribute(keyAttribut).get();
								field.setDataInitialLoaded(keyValue + "");
							}
						}
					}else{
						field.setDataInitialLoaded(resultReturn + "");
					}
				}
			}
		} catch (Exception e) {
			logger.error("loadDynamicParteorField Error");
		}
		return field;
	}

	@Override
	public String runFormValidationJSScript(String serverTemplateId, String domainId, Long taskId, String script, Map<String, Object> variables) {
		if(!script.trim().endsWith("return null;")){
			script += "\n return null;";
		}
		try {
			 UserTaskServicesClient taskClient = getClient(serverTemplateId, domainId, UserTaskServicesClient.class);
			 ProcessServicesClient processService = getClient(serverTemplateId, domainId, ProcessServicesClient.class);
			 TaskInstance task = taskClient.getTaskInstance(domainId, taskId, true, true, false);
			 Map<String, Object> variableProcess = processService.getProcessInstanceVariables(domainId, task.getProcessInstanceId());
			 variables = mergeVariables(variables, variableProcess);
			 ScriptEngineManager factory = new ScriptEngineManager();
	         ScriptEngine engine = factory.getEngineByName("JavaScript");
	         ScriptContext scriptContext = new SimpleScriptContext();
	         scriptContext.setBindings(new SimpleBindings(variables), ScriptContext.ENGINE_SCOPE);
	         engine.eval("function validate() {\n" + script +"\n}", scriptContext);
	         engine.setContext(scriptContext);
	         Invocable invocable = (Invocable) engine;
	         Object result = invocable.invokeFunction("validate");
	         if(null != result && !"".equals(result)){
	        	 result = convertIntoJavaObject(result);
        		 if(result instanceof List){
        			 List arrayResult = (List) result;
        			 if(arrayResult.size() > 0){
            			 if (isMessageParteor(arrayResult.get(0) + "")) {
            				 if(arrayResult.size() == 1){
            					 result = ComponentMessageResource.getInstance().getText(arrayResult.get(0) + "", new Object[]{});
            				 }else if(arrayResult.size() > 1){
            					 if(arrayResult.get(1) instanceof List){
            						 result = ComponentMessageResource.getInstance().getText(arrayResult.get(0) + "", ((List) arrayResult.get(1)).toArray()); 
            					 }
            				 }
     	 				 }
            		 }
        		 }
	         }
	         return (null == result ? "" : result.toString());
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			String message = e.getMessage();
			if(message != null && !"".endsWith(message))
				return message;
			else
				return "Erreur d'exécution validation script";
		}
	}

	private boolean isMessageParteor(String message){
		if (message.length() > 5 && !message.trim().contains(" ")) {
			String code = message.substring(message.length() - 4, message.length());
			try {
		        Double.parseDouble(code);
		        return true;
		    } catch (NumberFormatException nfe) {
		        return false;
		    }
		}
		return false;
	}
	
	private <T> T getClient(final String serverTemplateId, final String containerId, final Class<T> clientType) {
		KieServicesClient client = getKieServicesClient(serverTemplateId, containerId);
		return client.getServicesClient(clientType);
	}

	private KieServicesClient getKieServicesClient(final String serverTemplateId, final String containerId) {
		KieServicesClient client = kieServerIntegration.getServerClient(serverTemplateId, containerId);
		if (client == null) {
			throw new RuntimeException("No connection to '" + serverTemplateId + "' server(s). Server template configuration requires container '" + containerId + "' to be configured and started");
		}
		return client;
	}
	
	private Map<String, Object> mergeVariables(Map<String, Object> formValues, Map<String, Object> processVariables){
		HashMap<String, Object> shallowCopy = shallowCopy(processVariables);
		Iterator<Entry<String, Object>> it = formValues.entrySet().iterator();;
    	while (it.hasNext()) {
    		Entry<String, Object> entry = it.next();
    		String key = entry.getKey();
			Object value = entry.getValue();
			if (key.contains("/")) {
				String parentVariable = key.substring(0, key.indexOf("/"));
				String attributeParent = key.substring(key.indexOf("/") + 1, key.length());
				Object parentVariableValue = shallowCopy.get(parentVariable);
				if(null != parentVariableValue){
					try {
						BeanUtils.setProperty(parentVariableValue, attributeParent, value);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}else{
				Object variableValue = shallowCopy.get(key);
				if(null != variableValue){
					shallowCopy.replace(key, value);
				}
			}
		}
		return shallowCopy;
	}
	
	private HashMap<String, Object> shallowCopy(Map<String, Object> map){
		HashMap<String, Object> shallowCopy = new HashMap<String, Object>();
		Set<Entry<String, Object>> entries = map.entrySet();
		for (Map.Entry<String, Object> mapEntry : entries) {
		    shallowCopy.put(mapEntry.getKey(), mapEntry.getValue());
		}
		return shallowCopy;
	}
	
	private static Object convertIntoJavaObject(Object scriptObj) {
	    if (null != scriptObj && scriptObj instanceof ScriptObjectMirror) {
	        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) scriptObj;
	        if (scriptObjectMirror.isArray()) {
	            List<Object> list = new ArrayList<Object>();
	            for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet()) {
	                list.add(convertIntoJavaObject(entry.getValue()));
	            }
	            return list;
	        } else {
	            Map<String, Object> map = new HashMap<>();
	            for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet()) {
	                map.put(entry.getKey(), convertIntoJavaObject(entry.getValue()));
	            }
	            return map;
	        }
	    } else {
	        return scriptObj;
	    }
	}
}
