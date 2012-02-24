/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.referencing.factory;

import java.lang.annotation.Target;
import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotation for factories having hard-coded hints. Inspecting the annotation on a class
 * is much cheaper than asking for the implementation hints on an instance of that class,
 * especially when the instance requires a connection to a database (e.g. the EPSG factories).
 *
 * @todo Maybe we should provide a generalized form of this annotation in a public package.
 *       An alternative would be to use reflection for inspecting some static flavor of
 *       <code>getImplementationHints()</code>.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementationHints {
    /**
     * The value of {@link org.geotoolkit.factory.Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * which can not be null.
     *
     * @return Whatever the annotated factory forces longitude to be before latitude.
     */
    boolean forceLongitudeFirst();
}
