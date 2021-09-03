package org.kie.workbench.common.forms.dynamic.service.shared;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Remote
public interface ParteorComponentDataService {
	boolean checkDataExist(String methodClassMappingParteor, String keyMapping, Object value);
    
    public HashMap findLiveSearch(String methodClassMappingParteor, String keyMapping, String valueMapping, String pattern, int maxResults);
    
    public Map loadParteorFieldConfig(Object methodClassMappingParteor, Object keyMapping, Object valueMapping);
    
    public FieldDefinition loadDynamicParteorField(FieldDefinition fieldDefinition, HashMap formData);
    
    public HashMap setLabelAndParteorFieldMapping(String labelClassMapping);
    
    public String runFormValidationJSScript(String serverTemplateId, String domainId, Long taskId, String script, Map<String, Object> variables);
}
