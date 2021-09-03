package org.kie.workbench.common.forms.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FieldLabelData implements Serializable {
	
	String fieldName;
	String fieldLabel; 
	String fieldMapping;
	
	public FieldLabelData(String fieldName, String fieldLabel, String fieldMapping) {
		this.fieldName = fieldName;
		this.fieldLabel = fieldLabel;
		this.fieldMapping = fieldMapping;
	}

	public FieldLabelData() {
	}

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public String getFieldMapping() {
		return fieldMapping;
	}
	public void setFieldMapping(String fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
}
