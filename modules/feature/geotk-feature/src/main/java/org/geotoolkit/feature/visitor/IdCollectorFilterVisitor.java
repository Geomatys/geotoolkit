/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.visitor;

import java.util.Set;

import org.geotoolkit.filter.visitor.DefaultFilterVisitor;

import org.opengis.filter.Id;

/**
 * Gather up all FeatureId strings into a provided HashSet.
 * <p>
 * Example:<code>Set<String> fids = (Set<String>) filter.accept( IdCollectorFilterVisitor.ID_COLLECTOR, new HashSet() );</code>
 */
public class IdCollectorFilterVisitor extends DefaultFilterVisitor {

    public static final IdCollectorFilterVisitor ID_COLLECTOR = new IdCollectorFilterVisitor(true);
    public static final IdCollectorFilterVisitor IDENTIFIER_COLLECTOR = new IdCollectorFilterVisitor(false);

    private final boolean collectStrings;
    
    private IdCollectorFilterVisitor(boolean collectStringIds){
        collectStrings = collectStringIds;
    }
    
    @Override
    public Object visit(Id filter, Object data) {
        final Set set = (Set) data;
        set.addAll( (collectStrings) ? filter.getIDs() : filter.getIdentifiers());
        return set;
    }
}
