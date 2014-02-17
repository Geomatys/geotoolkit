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

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractDelete implements Delete{

    protected String handle = null;
    protected Filter filter = null;
    protected Name typeName = null;

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
    public Name getTypeName() {
        return typeName;
    }

    @Override
    public void setTypeName(final Name type) {
        this.typeName = type;
    }

}
