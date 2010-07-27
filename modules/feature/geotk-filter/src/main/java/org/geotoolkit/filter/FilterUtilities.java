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

package org.geotoolkit.filter;

import org.geotoolkit.filter.visitor.PrepareFilterVisitor;
import org.geotoolkit.lang.Static;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

/**
 * Utility methods for filters.
 *
 * @author Johann Sorel (Geomatys)
 */
@Static
public final class FilterUtilities {

    /**
     * Avoid instanciation.
     */
    private FilterUtilities() {}

    /**
     * Prepare a filter against a given class.
     * @param filter : filter to optimize
     * @param objectClazz : target class against which to optimize
     * @return optimized filter
     */
    public static Filter prepare(Filter filter, Class objectClazz){
        if(filter == null) return null;
        final PrepareFilterVisitor visitor = new PrepareFilterVisitor(objectClazz);
        return (Filter) filter.accept(visitor, null);
    }

    /**
     * Generates a property name which caches the value accessor.
     * the returned PropertyName should not be used against objects of a different
     * class, the result will be unpredictable.
     *
     * @param exp : the property name to prepare
     * @param objectClazz : the target class against which this prepared property
     *      will be used.
     * @return prepared property name expression.
     */
    public static PropertyName prepare(PropertyName exp, Class objectClazz){
        return new CachedPropertyName(exp.getPropertyName(), objectClazz);
    }

}
