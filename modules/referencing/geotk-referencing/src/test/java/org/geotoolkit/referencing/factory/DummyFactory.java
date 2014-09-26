/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.CommonCRS;

import static org.junit.Assert.*;


/**
 * A dummy factory which returns {@link DefaultGeographicCRS#WGS84} for all codes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final strictfp class DummyFactory extends AbstractAuthorityFactory {
    /**
     * A threaded factory which creates new instances of {@link DummyFactory}.
     */
    static final strictfp class Threaded extends ThreadedAuthorityFactory {
        /**
         * All dummy factories created by this threaded factory,
         * including any factories having been disposed.
         */
        private final Queue<DummyFactory> factories = new ConcurrentLinkedQueue<>();

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
            assertFalse("Should be invoked outside synchronized block.", Thread.holdsLock(this));
            final DummyFactory factory = new DummyFactory();
            assertTrue(factories.add(factory));
            return factory;
        }

        /**
         * Returns a copy of the factories queue. This is returned as a list in order
         * to allows comparisons with {@link List#equals}.
         */
        final synchronized List<DummyFactory> factories() {
            return new ArrayList<>(factories);
        }
    }

    /**
     * {@code true} if this factory has been disposed by
     * an explicit call to the {@link #dispose} method.
     */
    private boolean disposed;

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
    public synchronized boolean isDisposed() {
        return disposed;
    }

    /**
     * Flags this factory as disposed.
     */
    @Override
    protected synchronized void dispose(boolean shutdown) {
        super.dispose(shutdown);
        disposed = true;
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
    public synchronized IdentifiedObject createObject(String code) throws FactoryException {
        if (disposed) {
            throw new FactoryException("Factory has been disposed.");
        }
        return CommonCRS.WGS84.normalizedGeographic();
    }
}
