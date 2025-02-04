/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core;

public interface ObjectProperty extends HasName, HasClassName, HasAnnotations, HasVisibility {

    boolean isMultiple();

    boolean isArray();

    void setMultiple(boolean multiple);

    boolean isBaseType();

    boolean isPrimitiveType();

    String getBag();
    
    void setBag(String bag);

    boolean isStatic();

    boolean isFinal();

    int getModifiers();

    void setClassName( String className );
    
    public Object getValue();
    
    public void setValue(Object value);
}