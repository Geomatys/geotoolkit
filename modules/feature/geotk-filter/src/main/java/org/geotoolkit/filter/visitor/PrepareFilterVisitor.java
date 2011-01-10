/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.filter.visitor;

import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.expression.PropertyName;

/**
 * Simplify and prepare the filter against a given target class.
 * All propertyName expression will be prepared against it.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PrepareFilterVisitor extends SimplifyingFilterVisitor{

    private final Class clazz;

    public PrepareFilterVisitor(final Class clazz){
        this.clazz = clazz;
    }

    @Override
    public Object visit(final PropertyName expression, final Object extraData) {
        return FilterUtilities.prepare(expression, clazz);
    }

}
