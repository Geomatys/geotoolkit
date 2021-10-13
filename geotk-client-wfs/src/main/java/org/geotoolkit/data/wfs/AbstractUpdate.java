/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.wfs;

import java.util.HashMap;
import java.util.Map;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class AbstractUpdate implements Update{

    protected final Map<PropertyType,Object> updates = new HashMap<>();
    protected GenericName typeName = null;
    protected String handle = null;
    protected Filter filter = null;
    protected CoordinateReferenceSystem crs = null;
    protected String inputFormat = null;

    @Override
    public Map<PropertyType,Object> updates() {
        return updates;
    }

    @Override
    public String getHandle() {
        return handle;
    }

    @Override
    public void setHandle(final String handle) {
        this.handle = handle;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    @Override
    public String getInputFormat() {
        return inputFormat;
    }

    @Override
    public void setInputFormat(final String format) {
        this.inputFormat = format;
    }

    @Override
    public GenericName getTypeName() {
        return typeName;
    }

    @Override
    public void setTypeName(final GenericName type) {
        this.typeName = type;
    }

}
