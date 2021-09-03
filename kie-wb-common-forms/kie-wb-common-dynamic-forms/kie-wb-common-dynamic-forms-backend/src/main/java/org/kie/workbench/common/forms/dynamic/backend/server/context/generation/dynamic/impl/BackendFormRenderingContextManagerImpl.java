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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.soup.project.datamodel.ParteorLibsScanner;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.ContextModelConstraintsExtractor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class BackendFormRenderingContextManagerImpl implements BackendFormRenderingContextManager {

	private static final Logger logger = LoggerFactory.getLogger(BackendFormRenderingContextManagerImpl.class);

	protected Map<Long, BackendFormRenderingContextImpl> contexts = new HashMap<>();

	protected FieldValueMarshallerRegistry registry;

	protected ContextModelConstraintsExtractor constraintsExtractor;

	@Inject
	public BackendFormRenderingContextManagerImpl(FieldValueMarshallerRegistry registry, ContextModelConstraintsExtractor constraintsExtractor) {
		this.registry = registry;
		this.constraintsExtractor = constraintsExtractor;
	}

	@Override
	public BackendFormRenderingContext registerContext(FormDefinition rootForm, Map<String, Object> formData, Map<String, Object> processVariable, ClassLoader classLoader, FormDefinition... nestedForms) {
		return registerContext(rootForm, formData, processVariable, classLoader, new HashMap<String, String>(), nestedForms);
	}

	@Override
	public BackendFormRenderingContext registerContext(FormDefinition rootForm, Map<String, Object> formData, Map<String, Object> processVariable, ClassLoader classLoader, Map<String, String> params, FormDefinition... nestedForms) {

		MapModelRenderingContext clientRenderingContext = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

		clientRenderingContext.setRootForm(rootForm);

		Arrays.stream(nestedForms).forEach(form -> clientRenderingContext.getAvailableForms().put(form.getId(), form));

		BackendFormRenderingContextImpl context = new BackendFormRenderingContextImpl(System.currentTimeMillis(), clientRenderingContext, formData, classLoader, params);
		try {
			classLoader.loadClass("com.prodaxis.sbc.shared.domain.Domain");
		} catch (Exception e) {
			// <Prodaxis>
			List<URL> urls = ParteorLibsScanner.getInstance().getParteorJarsUrl();
			classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), classLoader);
			// </Prodaxis>
		}
		Map<String, Object> clientFormData = new HashMap<>();

		rootForm.getFields().stream().filter(fieldDefinition -> !StringUtils.isEmpty(fieldDefinition.getBinding())).forEach(fieldDefinition -> {
			String fieldBinding = fieldDefinition.getBinding();
			Object value = formData.get(fieldBinding);
			if (null == value) {
				if(fieldBinding.indexOf("/") != -1){
					try {
						int separatorPosition = 0;
						separatorPosition = fieldBinding.indexOf("/");
						String nestedModelName = fieldBinding.substring(0, separatorPosition);
						String property = fieldBinding.substring(separatorPosition + 1);
						Object nestedModelValue = formData.get(nestedModelName);
						if(null == nestedModelValue && null != processVariable)
							nestedModelValue = processVariable.get(nestedModelName);
						if(null != nestedModelValue)
							value = BeanUtils.getProperty(nestedModelValue, property);
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				}else{
					if(null != processVariable)
						value = processVariable.get(fieldBinding);
				}
			}
			FieldValueMarshaller marshaller = registry.getMarshaller(fieldDefinition);
			if (marshaller != null) {
				marshaller.init(value, fieldDefinition, rootForm, context);
				context.getRootFormMarshallers().put(fieldDefinition.getBinding(), marshaller);
				value = marshaller.toFlatValue();
			}
			clientFormData.put(fieldBinding, value);
		});

		constraintsExtractor.readModelConstraints(clientRenderingContext, classLoader);

		clientRenderingContext.setModel(clientFormData);

		contexts.put(context.getTimestamp(), context);

		return context;
	}

	@Override
	public BackendFormRenderingContext updateContextData(long timestamp, Map<String, Object> formValues) {

		BackendFormRenderingContextImpl context = contexts.get(timestamp);

		if (context == null) {
			throw new IllegalArgumentException("Unable to find context with id '" + timestamp + "'");
		}

		FormDefinition rootForm = context.getRenderingContext().getRootForm();

		Map<String, Object> contextData = new HashMap<>();

		rootForm.getFields().stream().filter(fieldDefinition -> !StringUtils.isEmpty(fieldDefinition.getBinding())).forEach(fieldDefinition -> {
			Object value = formValues.get(fieldDefinition.getBinding());

			FieldValueMarshaller marshaller = context.getRootFormMarshallers().get(fieldDefinition.getBinding());
			if (marshaller != null) {
				value = marshaller.toRawValue(value);
			}
			contextData.put(fieldDefinition.getBinding(), value);
		});

		context.setFormData(contextData);

		return context;
	}

	@Override
	public BackendFormRenderingContext getContext(Long timestamp) {
		return contexts.get(timestamp);
	}

	@Override
	public boolean removeContext(Long timestamp) {
		return contexts.remove(timestamp) != null;
	}

	@Override
	public Object getClassInstance(long timestamp, String clazzName) {
		BackendFormRenderingContextImpl context = contexts.get(timestamp);
		if (context == null) {
			throw new IllegalArgumentException("Unable to find context with id '" + timestamp + "'");
		}
		Class clazz = null;
        try {
            clazz = context.getClassLoader().loadClass(clazzName);
        } catch (ClassNotFoundException e) {
            // Maybe the nested class it is not on the classLoader context... let's try on the app classloader
            try {
                clazz = Class.forName(clazzName);
            } catch (ClassNotFoundException e1) {
                logger.warn("Unable to find class '{}' on classLoader", clazzName);
            }
        }
        if (clazz != null) {
            try {
                return ConstructorUtils.invokeConstructor(clazz, new Object[0]);
            } catch (Exception e) {
                logger.warn("Unable to create instance for class {}: ", clazzName);
            }
        }
        throw new IllegalStateException("Unable to create instance for class " + clazzName);
	}
}
