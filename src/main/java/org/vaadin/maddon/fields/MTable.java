/*
 * Copyright 2014 mattitahvonenitmill.
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
package org.vaadin.maddon.fields;

import com.vaadin.ui.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.vaadin.maddon.ListContainer;

public class MTable<T> extends Table {

    private ListContainer<T> bic;
    private String[] pendingProperties;
    private String[] pendingHeaders;

    public MTable() {
    }
    
    public MTable(ListContainer<T> bic) {
    	this.bic = bic;
        setContainerDataSource(bic);
    }

    /**
     * Constructs a Table with explicit bean type. Handy for example if your
     * beans are JPA proxies or the table in empty when showing it initially.
     * 
     * @param type
     */
    public MTable(Class<T> type) {
        bic = new ListContainer<T>(type);
        setContainerDataSource(bic);
    }

    public MTable(T... beans) {
        this(new ArrayList<T>(Arrays.asList(beans)));
    }

    public MTable(Collection<T> beans) {
        this();
        if (beans != null) {
            if (beans instanceof List) {
                bic = new ListContainer<T>((List<T>) beans);
            } else {
                bic = new ListContainer<T>(new ArrayList<T>(beans));
            }
            setContainerDataSource(bic);
        }
    }

    public MTable<T> withProperties(String... visibleProperties) {
        if (isContainerInitialized()) {
            setVisibleColumns((Object[]) visibleProperties);
        } else {
            pendingProperties = visibleProperties;
            for (String string : visibleProperties) {
                addContainerProperty(string, String.class, "");
            }
        }
        return this;
    }

    private boolean isContainerInitialized() {
        return bic != null;
    }

    public MTable<T> withColumnHeaders(String... columnNamesForVisibleProperties) {
        if (isContainerInitialized()) {
            setColumnHeaders(columnNamesForVisibleProperties);
        } else {
            pendingHeaders = columnNamesForVisibleProperties;
            // Add headers to temporary indexed container, in case table is initially
            // empty
            for (String prop : columnNamesForVisibleProperties) {
                addContainerProperty(prop, String.class, "");
            }
        }
        return this;
    }

    public void addMValueChangeListener(MValueChangeListener<T> listener) {
        addListener(MValueChangeEvent.class, listener,
                MValueChangeEventImpl.VALUE_CHANGE_METHOD);
        // implicitly consider the table should be selectable
        setSelectable(true);
        // TODO get rid of this when 7.2 is out
        setImmediate(true);
    }

    public void removeMValueChangeListener(MValueChangeListener<T> listener) {
        removeListener(MValueChangeEvent.class, listener,
                MValueChangeEventImpl.VALUE_CHANGE_METHOD);
        setSelectable(hasListeners(MValueChangeEvent.class));
    }

    @Override
    protected void fireValueChange(boolean repaintIsNotNeeded) {
        super.fireValueChange(repaintIsNotNeeded);
        fireEvent(new MValueChangeEventImpl(this));
    }

    private void ensureBeanItemContainer(Collection<T> beans) {
        if (!isContainerInitialized()) {
            bic = new ListContainer(beans);
            if(pendingProperties != null) {
                setContainerDataSource(bic, Arrays.asList(pendingProperties));
                pendingProperties = null;
            } else {
                setContainerDataSource(bic);
            }
            if (pendingHeaders != null) {
                setColumnHeaders(pendingHeaders);
                pendingHeaders = null;
            }
        }
    }

    @Override
    public T getValue() {
        return (T) super.getValue();
    }

    @Override
    @Deprecated
    public void setMultiSelect(boolean multiSelect) {
        super.setMultiSelect(multiSelect);
    }

    public void addBeans(T... beans) {
        addBeans(Arrays.asList(beans));
    }

    public MTable addBeans(Collection<T> beans) {
        if (!beans.isEmpty()) {
            if (isContainerInitialized()) {
                bic.addAll(beans);
            } else {
                ensureBeanItemContainer(beans);
            }
        }
        return this;
    }

    public MTable setBeans(T... beans) {
        setBeans(new ArrayList<T>(Arrays.asList(beans)));
        return this;
    }

    public MTable setBeans(Collection<T> beans) {
        if (!isContainerInitialized() && !beans.isEmpty()) {
            ensureBeanItemContainer(beans);
        } else if (isContainerInitialized()) {
            bic.setCollection(beans);
        }
        return this;
    }

    public MTable<T> withFullWidth() {
        setWidth(100, Unit.PERCENTAGE);
        return this;
    }

    public MTable<T> withHeight(String height) {
        setHeight(height);
        return this;
    }

    public MTable<T> withFullHeight() {
        return withHeight("100%");
    }

    public MTable<T> withWidth(String width) {
        setWidth(width);
        return this;
    }

    public MTable<T> withCaption(String caption) {
        setCaption(caption);
        return this;
    }

}
