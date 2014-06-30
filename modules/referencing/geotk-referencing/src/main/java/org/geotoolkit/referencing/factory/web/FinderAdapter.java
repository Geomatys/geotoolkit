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
package org.geotoolkit.referencing.factory.web;

import java.util.Locale;

import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;

import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;


/**
 * Wraps the {@link IdentifiedObjectFinder} provided by the factory wrapped by the URN or
 * HTTP factory in order to append the prefix before the identifier of the object found.
 * For example if we didn't provided this adapter, then the following code:
 *
 * {@preformat java
 *     CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
 *     String identifier = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, crs);
 * }
 *
 * would produce {@code "EPSG:4326"}, while we want {@code "urn:ogc:def:crs:epsg:7.1:4326"}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.07
 * @module
 */
class FinderAdapter extends IdentifiedObjectFinder {
    /**
     * The finder on which to delegate the work.
     */
    private final IdentifiedObjectFinder finder;

    /**
     * Creates a finder for the underlying factory, which shall be the
     * "All authority" factory wrapped by the URN or HTTP factory.
     */
    FinderAdapter(final AbstractAuthorityFactory factory, Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        super(factory, type);
        finder = factory.getIdentifiedObjectFinder(type);
    }

    /**
     * Propagates the configuration change to the underlying finder.
     *
     * @since 3.18
     */
    @Override
    public void setComparisonMode(final ComparisonMode mode) {
        finder.setComparisonMode(mode);
        super .setComparisonMode(mode);
    }

    /**
     * Propagates the configuration change to the underlying finder.
     */
    @Override
    public final void setFullScanAllowed(final boolean fullScan) {
        finder.setFullScanAllowed(fullScan);
        super .setFullScanAllowed(fullScan);
    }

    /**
     * Delegates the lookup to the wrapped finder with no change.
     */
    @Override
    public final IdentifiedObject find(final IdentifiedObject object) throws FactoryException {
        return finder.find(object);
    }

    /**
     * Find the identifier for the given object, and prepend the URN or HTTP path before it.
     */
    @Override
    public final String findIdentifier(final IdentifiedObject object) throws FactoryException {
        final IdentifiedObject candidate = find(object);
        if (candidate != null) {
            ReferenceIdentifier identifier = IdentifiedObjects.getIdentifier(object, null);
            if (identifier != null || (identifier = object.getName()) != null) {
                String code      = identifier.getCode();
                String codespace = identifier.getCodeSpace();
                if (code != null && codespace != null) {
                    codespace = codespace.toLowerCase(Locale.US);
                    return path(object, identifier, codespace).append(code).toString();
                }
            }
        }
        return null;
    }

    /**
     * Builds the path to the prepend before the code. Include the character separator
     * between the path and the code, but does not include the code itself.
     */
    StringBuilder path(final IdentifiedObject object, final ReferenceIdentifier identifier, final String codespace) {
        final URN_Type type = URN_Type.getInstance(object.getClass());
        final StringBuilder buffer = new StringBuilder("urn:ogc:def:")
                .append(type).append(':').append(codespace).append(':');
        final String version = identifier.getVersion();
        if (version != null) {
            buffer.append(version).append(':');
        }
        return buffer;
    }
}
