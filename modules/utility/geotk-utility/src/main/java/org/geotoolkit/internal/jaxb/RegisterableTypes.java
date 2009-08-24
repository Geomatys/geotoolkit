/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb;

import java.util.Collection;


/**
 * Declares the classes of objects to be marshalled using a default {@code MarshallerPool}. This
 * interface is not strictly necessary for marshalling a Geotk object using JAXB, but makes
 * the job easier by allowing {@code MarshallerPool} to configure the JAXB context automatically.
 * To allow such automatic configuration, modules must declare the implemented base classes in the
 * following file:
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.xml.Marshallable
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public interface RegisterableTypes {
    /**
     * Adds to the given collection every types that should be given to
     * the initial JAXB context.
     *
     * @param addTo The collection in which to add new types.
     */
    void getTypes(final Collection<Class<?>> addTo);
}
