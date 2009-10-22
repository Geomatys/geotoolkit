/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display.canvas;

import java.awt.RenderingHints;


/**
 * The key for hints enumerated in the {@link AbstractCanvas} insteface.
 *
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @module pending
 */
public final class HintKey extends RenderingHints.Key {
    /**
     * Base class of all values for this key.
     */
    private final Class valueClass;

    /**
     * Constructs a new key.
     *
     * @param id An ID. Must be unique for all instances of {@link Key}.
     * @param valueClass Base class of all valid values.
     */
    public HintKey(final int id, final Class valueClass) {
        super(id);
        this.valueClass = valueClass;
    }

    /**
     * Returns {@code true} if the specified object is a valid value for this key.
     *
     * @param  value The object to test for validity.
     * @return {@code true} if the value is valid; {@code false} otherwise.
     */
    @Override
    public boolean isCompatibleValue(final Object value) {
        if (value == null) {
            return false;
        }
        if (!valueClass.isAssignableFrom(value.getClass())) {
            return false;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() >= 0;
        }
        return true;
    }
}
