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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class CharacterListBoxFieldDefinition extends ListBoxBaseDefinition<CharacterSelectorOption, Character> {

    @FormField(
            labelKey = "selector.options",
            afterElement = "label"
    )
    protected List<CharacterSelectorOption> options = new ArrayList<>();

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.forms.editor.client.editor.dataProviders.SelectorOptionsProvider")
    @FormField(
            type = ListBoxFieldType.class,
            labelKey = "defaultValue",
            afterElement = "options",
            settings = {@FieldParam(name = "relatedField", value = "options")}
    )
    protected Character defaultValue;

    public CharacterListBoxFieldDefinition() {
        super(Character.class.getName());
    }

    @Override
    public List<CharacterSelectorOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<CharacterSelectorOption> options) {
        this.options = options;
    }

    @Override
    public Character getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Character defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List getSelectorOptions(){
    	return options;
    }

    public void setSelectorOptions(List options){
    	this.options = options;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CharacterListBoxFieldDefinition that = (CharacterListBoxFieldDefinition) o;

        if (options != null ? !options.equals(that.options) : that.options != null) {
            return false;
        }
        return defaultValue != null ? defaultValue.equals(that.defaultValue) : that.defaultValue == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
