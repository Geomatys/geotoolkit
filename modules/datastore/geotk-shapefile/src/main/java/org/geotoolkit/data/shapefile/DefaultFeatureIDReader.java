/*
 *    Geotoolkit  An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data.shapefile;

import java.util.concurrent.atomic.AtomicLong;

import org.opengis.feature.type.FeatureType;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Simple implementation of a feature id generator which
 * concatenate a String with a number.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultFeatureIDReader implements FeatureIDReader {

    private final String base;
    private final AtomicLong inc = new AtomicLong();

    /**
     * This constructor will use the local part of the type as a
     * base string for ids.
     *
     * @param type the feature type
     */
    public DefaultFeatureIDReader(final FeatureType type) {
        this(type.getName().getLocalPart());
    }

    /**
     * @param base string use as start element of the generated ids
     */
    public DefaultFeatureIDReader(final String base) {
        ensureNonNull("base string", base);
        this.base = base + ".";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String next() {
        return base + inc.incrementAndGet();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
    }
}
