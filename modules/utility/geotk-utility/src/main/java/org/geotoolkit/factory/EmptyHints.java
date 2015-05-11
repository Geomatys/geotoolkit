/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.util.Map;
import java.awt.RenderingHints;

/**
 * An immutable empty set of hints.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
final class EmptyHints extends Hints {
    /**
     * Creates an empty instance.
     */
    EmptyHints() {
        super(false);
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void add(RenderingHints hints) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     */
    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void putAll(Map<?,?> map) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a modifiable copy.
     */
    @Override
    public Hints clone() {
        return new Hints(false);
    }
}
