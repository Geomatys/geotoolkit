/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.display.axis;

import java.awt.RenderingHints;


/**
 * Rendering hints for tick graduation.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
final class RenderingHintKey extends RenderingHints.Key {
    /**
     * The required base class.
     */
    private final Class<?> type;

    /**
     * Construct a rendering hint key.
     */
    protected RenderingHintKey(final Class<?> type, final int key) {
        super(key);
        this.type = type;
    }

    /**
     * Returns {@code true} if the specified object is a valid value for this key.
     */
    @Override
    public boolean isCompatibleValue(final Object value) {
        return value!=null && type.isAssignableFrom(value.getClass());
    }
}
