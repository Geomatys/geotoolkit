/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Arrays;
import java.util.Iterator;
import static org.junit.Assert.*;


/**
 * An implementation of {@link FactoryIteratorProvider} over the {@link DummyFactory}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
final class DummyFactoryIteratorProvider implements FactoryIteratorProvider {
    /**
     * {@code true} for iterating over the first half of examples, or {@code false}
     * for iterating over the second half.
     */
    private final boolean firstHalf;

    /**
     * Creates a new instance of the dummy factory iterator provider.
     *
     * @param firstHalf {@code true} for iterating over the first half of examples,
     *        or {@code false} for iterating over the second half.
     */
    public DummyFactoryIteratorProvider(final boolean firstHalf) {
        this.firstHalf = firstHalf;
    }

    /**
     * Returns an iterator over all {@link DummyFactory}.
     *
     * @param <T> The category for the factories to be returned.
     */
    @Override
    public <T> Iterator<T> iterator(final Class<T> category) {
        assertEquals(DummyFactory.class, category);
        final DummyFactory[] factories;
        if (firstHalf) {
            factories = new DummyFactory[] {
                new Example1(),
                new Example2(),
            };
        } else {
            factories = new DummyFactory[] {
                new Example3(),
                new Example4()
            };
        }
        @SuppressWarnings("unchecked")
        final Iterator<T> it = (Iterator) Arrays.asList(factories).iterator();
        return it;
    }
}
