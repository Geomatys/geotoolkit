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
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.FactoryException;

import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import static org.junit.Assert.*;


/**
 * A dummy factory which returns {@link DefaultGeographicCRS#WGS84} for all codes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class DummyFactory extends AbstractAuthorityFactory {
    /**
     * A threaded factory which creates new instances of {@link DummyFactory}.
     */
    static final class Threaded extends ThreadedAuthorityFactory {
        /**
         * All dummy factories created by this threaded factory,
         * including any factories having been disposed.
         */
        private final Queue<DummyFactory> factories = new ConcurrentLinkedQueue<DummyFactory>();

        /**
         * Creates a new threaded factory.
         */
        Threaded() {
            super(EMPTY_HINTS);
        }

        /**
         * Creates a dummy factory. The new factory is added to {@link #factories} queue.
         */
        @Override
        protected AbstractAuthorityFactory createBackingStore() {
            final DummyFactory factory = new DummyFactory();
            assertTrue(factories.add(factory));
            return factory;
        }

        /**
         * Returns a copy of the factories queue. This is returned as a list in order
         * to allows comparisons with {@link List#equals}.
         */
        final List<DummyFactory> factories() {
            return new ArrayList<DummyFactory>(factories);
        }
    }

    /**
     * {@code true} if this factory has been disposed by
     * an explicit call to the {@link #dispose} method.
     */
    private volatile boolean disposed;

    /**
     * Creates a new dummy factory.
     */
    DummyFactory() {
        super(EMPTY_HINTS);
    }

    /**
     * Returns {@code true} if this factory has been disposed
     * by an explicit call to the {@link #dispose} method.
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Flags this factory as disposed.
     */
    @Override
    protected void dispose(boolean shutdown) {
        disposed = true;
        super.dispose(shutdown);
    }

    /**
     * Not used.
     */
    @Override
    public Citation getAuthority() {
        return Citations.GEOTOOLKIT;
    }

    /**
     * Not used.
     */
    @Override
    public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> type) {
        return Collections.emptySet();
    }

    /**
     * Not used.
     */
    @Override
    public InternationalString getDescriptionText(String code) {
        return new SimpleInternationalString("Description of " + code);
    }

    /**
     * Returned value is not relevant for our tests.
     */
    @Override
    public IdentifiedObject createObject(String code) throws FactoryException {
        if (disposed) throw new FactoryException();
        return DefaultGeographicCRS.WGS84;
    }
}
