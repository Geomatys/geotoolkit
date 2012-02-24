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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Map;
import java.util.HashMap;

import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;

import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;


/**
 * Base class for the tests which will require a connection to an EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public abstract strictfp class EpsgFactoryTestBase extends ReferencingTestBase {
    /**
     * {@code true} for a given type if we have been able to fetch its factory, {@code false}
     * if we failed, or {@code null} if we didn't tried yet. This is used in order to avoid
     * asking for the connection again.
     */
    private static final Map<Class<? extends AbstractAuthorityFactory>, Boolean> status = new HashMap<>();

    /**
     * The class of the factory being tested.
     */
    private final Class<? extends AbstractAuthorityFactory> type;

    /**
     * The factory to test, or {@code null} if we can't connect to the
     * database for the platform the test are running on.
     */
    protected AbstractAuthorityFactory factory;

    /**
     * Creates a new abstract test base.
     *
     * @param type The class of the factory being tested.
     */
    protected EpsgFactoryTestBase(final Class<? extends AbstractAuthorityFactory> type) {
        this.type = type;
    }

    /**
     * Gets the factory which will be used for the tests. This method fetches the factory
     * from {@link AuthorityFactoryFinder} for the type given to the constructor. If no
     * factory is found for that type, then {@link #factory} is left to {@code null}.
     */
    @Before
    public final void initialize() {
        synchronized (status) {
            final Boolean state = status.get(type);
            if (Boolean.FALSE.equals(state)) {
                // A previous attempt failed to get the factory. Don't try again.
                return;
            }
            Boolean success = Boolean.FALSE;
            final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, type);
            try {
                factory = (AbstractAuthorityFactory) AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
                success = Boolean.TRUE;
            } catch (FactoryNotFoundException exception) {
                if (Boolean.TRUE.equals(state)) {
                    // It worked in a previous attempt, so it should not fail now...
                    throw exception;
                }
            }
            status.put(type, success);
        }
    }

    /**
     * Returns the first identifier for the specified object.
     *
     * @param object The object for which to get the identifier.
     * @return The first identifier of the given object.
     */
    protected static String getIdentifier(final IdentifiedObject object) {
        return object.getIdentifiers().iterator().next().getCode();
    }
}
