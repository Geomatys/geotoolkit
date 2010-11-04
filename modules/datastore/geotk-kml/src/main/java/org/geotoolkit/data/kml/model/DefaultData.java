/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultData extends DefaultAbstractObject implements Data {

    private String name;
    private Object displayName;
    private String value;
    private List<Object> dataExtensions;

    /**
     *
     */
    public DefaultData() {
        this.dataExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param displayName
     * @param value
     * @param dataExtensions
     */
    public DefaultData(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, Object displayName, String value, List<Object> dataExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.name = name;
        this.displayName = displayName;
        this.value = value;
        this.dataExtensions = (dataExtensions == null) ? EMPTY_LIST : dataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getDisplayName() {
        return this.displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getDataExtensions() {
        return this.dataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDataExtensions(List<Object> dataExtensions) {
        this.dataExtensions = (dataExtensions == null) ? EMPTY_LIST : dataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
}
