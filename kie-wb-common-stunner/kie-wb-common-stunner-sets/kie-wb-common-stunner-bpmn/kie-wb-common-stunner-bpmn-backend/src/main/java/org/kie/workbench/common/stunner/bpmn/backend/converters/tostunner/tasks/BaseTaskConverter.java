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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

import java.util.Optional;

import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNElementDecorators;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.AbstractConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BusinessRuleTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.GenericServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ScriptTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ExecutionTarget;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DecisionName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DmnModelName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Namespace;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public abstract class BaseTaskConverter<U extends BaseUserTask<S>, S extends BaseUserTaskExecutionSet>
        extends AbstractConverter implements NodeConverter<Task> {

    protected final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public BaseTaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory,
                             Mode mode) {
        super(mode);
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @Override
    public Result<BpmnNode> convert(org.eclipse.bpmn2.Task task) {
        return Match.of(Task.class, BpmnNode.class)
                .when(org.eclipse.bpmn2.BusinessRuleTask.class, this::businessRuleTask)
                .when(org.eclipse.bpmn2.ScriptTask.class, this::scriptTask)
                .when(org.eclipse.bpmn2.UserTask.class, this::userTask)
                .when(org.eclipse.bpmn2.ServiceTask.class, this::serviceTaskResolver)
                .whenExactly(org.eclipse.bpmn2.impl.TaskImpl.class, this::defaultTaskResolver)
                .missing(ManualTask.class)
                .missing(SendTask.class)
                .missing(ReceiveTask.class)
                .orElse(this::defaultTaskResolver)
                .inputDecorator(BPMNElementDecorators.flowElementDecorator())
                .outputDecorator(BPMNElementDecorators.bpmnNodeDecorator())
                .mode(getMode())
                .apply(task);
    }

    private BpmnNode jbpmServiceTask(org.eclipse.bpmn2.Task task) {
        return propertyReaderFactory
                .ofCustom(task)
                .map(p -> {
                    final Node<View<ServiceTask>, Edge> node = factoryManager.newNode(task.getId(), ServiceTask.class);
                    final ServiceTask definition = node.getContent().getDefinition();
                    definition.setName(p.getServiceTaskName());
                    definition.getTaskType().setRawType(p.getServiceTaskName());
                    definition.setDescription(p.getServiceTaskDescription());
                    definition.setCategory(p.getServiceTaskCategory());
                    definition.setDefaultHandler(p.getServiceTaskDefaultHandler());

                    definition.setGeneral(new TaskGeneralSet(
                            new Name(p.getName()),
                            new Documentation(p.getDocumentation())
                    ));

                    definition.setDataIOSet(new DataIOSet(
                            p.getAssignmentsInfo()
                    ));

                    definition.setExecutionSet(new ServiceTaskExecutionSet(
                            new TaskName(p.getTaskName()),
                            new IsAsync(p.isAsync()),
                            new AdHocAutostart(p.isAdHocAutoStart()),
                            new OnEntryAction(p.getOnEntryAction()),
                            new ExecutionTarget(p.getExecutionTarget()),
                            new OnExitAction(p.getOnExitAction()),
                            new SLADueDate(p.getSlaDueDate())
        ));

                    definition.setSimulationSet(p.getSimulationSet());

                    node.getContent().setBounds(p.getBounds());

                    definition.setDimensionsSet(p.getRectangleDimensionsSet());
                    definition.setBackgroundSet(p.getBackgroundSet());
                    definition.setFontSet(p.getFontSet());

                    return BpmnNode.of(node, p);
                })
                .orElseGet(() -> noneTask(task));
    }

    BpmnNode bpmnServiceTask(org.eclipse.bpmn2.ServiceTask task) {
        Node<View<GenericServiceTask>, Edge> node = factoryManager.newNode(task.getId(), GenericServiceTask.class);

        GenericServiceTask definition = node.getContent().getDefinition();
        GenericServiceTaskPropertyReader p = propertyReaderFactory.of(task);

        if (p == null) {
            throw new NullPointerException(task.getClass().getCanonicalName());
        }

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new GenericServiceTaskExecutionSet(
                new GenericServiceTaskInfo(p.getGenericServiceTask()),
                p.getAssignmentsInfo(),
                new AdHocAutostart(p.isAdHocAutostart()),
                new IsAsync(p.isAsync()),
                new IsMultipleInstance(p.isMultipleInstance()),
                new MultipleInstanceExecutionMode(p.isSequential()),
                new MultipleInstanceCollectionInput(p.getCollectionInput()),
                new MultipleInstanceDataInput(p.getDataInput()),
                new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                new MultipleInstanceDataOutput(p.getDataOutput()),
                new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new SLADueDate(p.getSLADueDate())));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setSimulationSet(p.getSimulationSet());

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        return BpmnNode.of(node, p);
    }

    private BpmnNode businessRuleTask(org.eclipse.bpmn2.BusinessRuleTask task) {
        Node<View<BusinessRuleTask>, Edge> node = factoryManager.newNode(task.getId(), BusinessRuleTask.class);

        BusinessRuleTask definition = node.getContent().getDefinition();
        BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        RuleLanguage ruleLanguage = new RuleLanguage(p.getImplementation());
        RuleFlowGroup ruleFlowGroup = null;
        Namespace namespace = null;
        DecisionName decisionName = null;
        DmnModelName dmnModelName = null;

        if (ruleLanguage.getValue().equals(RuleLanguage.DRL)) {
            ruleFlowGroup = new RuleFlowGroup(p.getRuleFlowGroup());
            namespace = new Namespace();
            decisionName = new DecisionName();
            dmnModelName = new DmnModelName();
        } else if (ruleLanguage.getValue().equals(RuleLanguage.DMN)) {
            ruleFlowGroup = new RuleFlowGroup();
            namespace = new Namespace(p.getNamespace());
            decisionName = new DecisionName(p.getDecisionName());
            dmnModelName = new DmnModelName(p.getDmnModelName());
        }

        definition.setExecutionSet(new BusinessRuleTaskExecutionSet(
                new RuleLanguage(p.getImplementation()),
                ruleFlowGroup,
                namespace,
                decisionName,
                dmnModelName,
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart()),
                new SLADueDate(p.getSlaDueDate())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node, p);
    }

    private BpmnNode scriptTask(org.eclipse.bpmn2.ScriptTask task) {
        Node<View<ScriptTask>, Edge> node = factoryManager.newNode(task.getId(), ScriptTask.class);

        ScriptTask definition = node.getContent().getDefinition();
        ScriptTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new ScriptTaskExecutionSet(
                new Script(p.getScript()),
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        definition.setSimulationSet(p.getSimulationSet());

        return BpmnNode.of(node, p);
    }

    private BpmnNode userTask(org.eclipse.bpmn2.UserTask task) {
        Node<View<U>, Edge> node = createNode(task.getId());

        U definition = node.getContent().getDefinition();
        UserTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setSimulationSet(
                p.getSimulationSet()
        );

        definition.setExecutionSet(createUserTaskExecutionSet(p));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node, p);
    }

    private BpmnNode defaultTaskResolver(Task task) {
        //in case serviceTaskName attribute is present handle as a Service Task, default is a None Task
        return Optional.ofNullable(CustomAttribute.serviceTaskName.of(task).get())
                .filter(StringUtils::nonEmpty)
                .filter(name -> propertyReaderFactory.ofCustom(task).isPresent())
                .map(name -> jbpmServiceTask(task))
                .orElseGet(() -> noneTask(task));
    }

    private BpmnNode serviceTaskResolver(final Task task) {
        org.eclipse.bpmn2.ServiceTask serviceTask = (org.eclipse.bpmn2.ServiceTask) task;
        if (StringUtils.nonEmpty(CustomAttribute.serviceImplementation.of(task).get())
                || StringUtils.nonEmpty(serviceTask.getImplementation())) {
            return bpmnServiceTask(serviceTask);
        } else {
            return jbpmServiceTask(task);
        }
    }

    private BpmnNode noneTask(Task task) {
        Node<View<NoneTask>, Edge> node = factoryManager.newNode(task.getId(), NoneTask.class);
        TaskPropertyReader p = propertyReaderFactory.of(task);

        NoneTask definition = node.getContent().getDefinition();

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new EmptyTaskExecutionSet());

        definition.setSimulationSet(
                p.getSimulationSet()
        );

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node, p);
    }

    protected abstract Node<View<U>, Edge> createNode(String id);

    protected abstract S createUserTaskExecutionSet(UserTaskPropertyReader p);
}
