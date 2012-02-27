/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.lang;


/**
 * Base class of all classes that are builder for other objects.
 * Subclasses provides an arbitrary amount of setter, together with a single {@link #build()}
 * method implementation which create the object using the values previously set.
 *
 * @param  <T> The type of objects to build.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public abstract class Builder<T> {
    /**
     * Creates an empty builder. The default values of all fields are implementation dependent.
     */
    protected Builder() {
    }

    /**
     * Creates the objects from the value previously set.
     *
     * @return The new object created from the user values.
     */
    public abstract T build();
}
