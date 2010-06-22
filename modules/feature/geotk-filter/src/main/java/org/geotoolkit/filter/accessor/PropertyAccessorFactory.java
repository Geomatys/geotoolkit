/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.accessor;

import org.geotoolkit.factory.Hints;

/**
 * Factory used to create instances of {@link PropertyAccessor}
 *
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public interface PropertyAccessorFactory {

    /**
     * The priority of this factory, since several factory might
     * be able to handle a given property path, the most restrictive
     * factory should have the highest priority.
     * 
     * @return int, higher value for higher priority.
     */
    int getPriority();

    /**
     * Creates a property accessor for a particular class.
     *
     * @param type The type of object to be accessed.
     * @param xpath The xpath expression to evaluate.
     * @param target The kind of result we are expecting (ie Geometry)
     * @param hints Hints to be used when creatign the accessor.
     *
     * @return The property accessor, or <code>null</code> if this factory cannot create
     * an accessor for the specified type.
     */
    PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target, Hints hints);
}
