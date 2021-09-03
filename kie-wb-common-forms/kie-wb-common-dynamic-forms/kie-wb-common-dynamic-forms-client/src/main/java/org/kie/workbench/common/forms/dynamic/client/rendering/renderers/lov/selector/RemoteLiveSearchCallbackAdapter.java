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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchEntry;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;

public class RemoteLiveSearchCallbackAdapter<TYPE, T> implements RemoteCallback, ErrorCallback {
    
    LiveSearchCallback<Object> liveSearchCallback;
    
    protected FieldDefinition field;

    public RemoteLiveSearchCallbackAdapter(LiveSearchCallback<Object> callback, FieldDefinition field) {
        this.liveSearchCallback = callback;
        this.field = field;
    }

    @Override
    public boolean error(Object message, Throwable throwable) {
        return false;
    }

    @Override
    public void callback(Object response) {
        if(null != response){
            if(response instanceof HashMap){
            	HashMap responseMap = (HashMap) response;
            	LiveSearchResults<Object> entries = new LiveSearchResults<Object>();
            	Iterator iterator = responseMap.entrySet().iterator();
                while (iterator.hasNext()) {
                	Map.Entry entry = (Map.Entry) iterator.next();
                	entries.add(new LiveSearchEntry(entry.getKey(), entry.getValue() + ""));
                }
                liveSearchCallback.afterSearch(entries);
            }
        }
    }
    
}
