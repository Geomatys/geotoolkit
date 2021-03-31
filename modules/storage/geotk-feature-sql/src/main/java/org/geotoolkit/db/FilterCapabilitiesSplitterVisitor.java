/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db;

import org.geotoolkit.filter.visitor.AbstractVisitor;
import org.opengis.filter.capability.FilterCapabilities;

/**
 * Given a filter capability, this filter will divide a filter in a
 * pre and post filter.
 * The pre-filter can be used directly while the post-filter will have to
 * be handle in java.
 *
 * TODO
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterCapabilitiesSplitterVisitor extends AbstractVisitor<Object,Object> {

    private final FilterCapabilities capabilities;

    public FilterCapabilitiesSplitterVisitor(FilterCapabilities capabilities) {
        this.capabilities = capabilities;
    }
}
