/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ExecutionTarget;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "isAsync")
public class ServiceTaskExecutionSet implements BPMNPropertySet {

    @Property
    @SkipFormField
    @Valid
    protected TaskName taskName;

    @Property
    @FormField
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(afterElement = "adHocAutostart", settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(type = TextAreaFieldType.class, afterElement = "onEntryAction", helpMessageKey = "helpMessage")
    @Valid
    private ExecutionTarget executionTarget;
    
    @Property
    @FormField(afterElement = "executionTarget", settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnExitAction onExitAction;

    @Property
    @FormField(afterElement = "onExitAction")
    @Valid
    private SLADueDate slaDueDate;

    public ServiceTaskExecutionSet() {
        this(new TaskName("Service Task"),
             new IsAsync(),
             new AdHocAutostart(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new ExecutionTarget(),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",""))),
             new SLADueDate());
    }

    public ServiceTaskExecutionSet(final @MapsTo("taskName") TaskName taskName,
                                   final @MapsTo("isAsync") IsAsync isAsync,
                                   final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                   final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                   final @MapsTo("executionTarget") ExecutionTarget executionTarget,
                                   final @MapsTo("onExitAction") OnExitAction onExitAction,
                                   final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        this.taskName = taskName;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
        this.onEntryAction = onEntryAction;
        this.executionTarget = executionTarget;
        this.onExitAction = onExitAction;
        this.slaDueDate = slaDueDate;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(final TaskName taskName) {
        this.taskName = taskName;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(SLADueDate slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
      
    public ExecutionTarget getExecutionTarget() {
		return executionTarget;
	}

	public void setExecutionTarget(ExecutionTarget executionTarget) {
		this.executionTarget = executionTarget;
	}

	@Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(taskName),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(executionTarget),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServiceTaskExecutionSet) {
            ServiceTaskExecutionSet other = (ServiceTaskExecutionSet) o;
            return Objects.equals(taskName, other.taskName) &&
                    Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(adHocAutostart, other.adHocAutostart) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(executionTarget, other.executionTarget) &&
                    Objects.equals(onExitAction, other.onExitAction) &&
                    Objects.equals(slaDueDate, other.slaDueDate);
        }
        return false;
    }
}
