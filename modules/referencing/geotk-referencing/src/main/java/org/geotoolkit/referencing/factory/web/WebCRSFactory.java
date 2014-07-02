/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.factory.DirectAuthorityFactory;
import org.apache.sis.referencing.CommonCRS;


/**
 * The factory for {@linkplain CoordinateReferenceSystem coordinate reference systems} in the
 * {@code CRS} namespace. The format is usually <code>"CRS:</code><var>n</var><code>"</code>
 * where <var>n</var> is a number like 27, 83 or 84. However this factory is lenient and allows
 * the {@code CRS} part to be repeated as in {@code "CRS:CRS84"}. It also accepts {@code "OGC"}
 * as a synonymous of the {@code "CRS"} namespace. Examples:
 * <p>
 * <ul>
 *   <li>{@code "CRS:27"}</li>
 *   <li>{@code "CRS:83"}</li>
 *   <li>{@code "CRS:84"}</li>
 *   <li>{@code "CRS:CRS84"}</li>
 *   <li>{@code "OGC:CRS84"}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
@ThreadSafe
public class WebCRSFactory extends DirectAuthorityFactory implements CRSAuthorityFactory {
    /**
     * An optional prefix put in front of code. For example a code may be {@code "CRS84"}
     * instead of a plain {@code "84"}. This is useful in order to understand URN syntax
     * like {@code "urn:ogc:def:crs:OGC:1.3:CRS84"}. Must be uppercase for this implementation
     * (but parsing will be case-insensitive).
     */
    private static final String PREFIX = "CRS";

    /**
     * The map of pre-defined CRS.
     */
    private final Map<Integer,CoordinateReferenceSystem> crsMap = new TreeMap<>();

    /**
     * Constructs a default factory for the {@code CRS} authority.
     */
    public WebCRSFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Constructs a factory for the {@code CRS} authority using the specified hints.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     */
    public WebCRSFactory(final Hints userHints) {
        super(userHints);
    }

    /**
     * Ensures that {@link #crsMap} is initialized. This method can't be invoked in the constructor
     * because the constructor is invoked while {@code FactoryFinder.scanForPlugins()} is still
     * running. Because the {@link #add} method uses factories for creating CRS objects, invoking
     * this method during {@code FactoryFinder.scanForPlugins()} execution may result in unexpected
     * behavior, like GEOT-935.
     */
    private synchronized void ensureInitialized() throws FactoryException {
        if (crsMap.isEmpty()) {
            add(84, "WGS84", CommonCRS.WGS84.ellipsoid());
            add(83, "NAD83", CommonCRS.NAD83.ellipsoid());
            add(27, "NAD27", CommonCRS.NAD27.ellipsoid());
        }
    }

    /**
     * Adds a geographic CRS from the specified parameters.
     *
     * @param code      The CRS code.
     * @param name      The CRS and datum name.
     * @param ellipsoid The ellipsoid.
     *
     * @throws FactoryException if factories failed to creates the CRS.
     */
    private void add(final int code, final String name, final Ellipsoid ellipsoid) throws FactoryException {
        assert Thread.holdsLock(this);
        final Map<String,Object> properties = new HashMap<>();
        final Citation authority = getAuthority();
        final String text = String.valueOf(code);
        properties.put(IdentifiedObject.NAME_KEY, name);
        properties.put(Identifier.AUTHORITY_KEY, authority);
        final GeodeticDatum datum = factories.getDatumFactory().createGeodeticDatum(
                properties, ellipsoid, CommonCRS.WGS84.primeMeridian());
        properties.put(IdentifiedObject.IDENTIFIERS_KEY, new NamedIdentifier[] {
                new NamedIdentifier(authority, text),
                new NamedIdentifier(authority, PREFIX + text)
        });
        final CoordinateReferenceSystem crs = factories.getCRSFactory().createGeographicCRS(
                properties, datum, DefaultEllipsoidalCS.GEODETIC_2D);
        if (crsMap.put(code, crs) != null) {
            throw new IllegalArgumentException(text);
        }
    }

    /**
     * Returns the authority for this factory, which is {@link Citations#CRS CRS}.
     */
    @Override
    public Citation getAuthority() {
        return Citations.CRS;
    }

    /**
     * Provides a complete set of the known codes provided by this authority. The returned set
     * contains only numeric identifiers like {@code "84"}, {@code "27"}, <i>etc</i>.
     * The authority name ({@code "CRS"}) is not included. This is consistent with the
     * {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory#getAuthorityCodes
     * codes returned by the EPSG factory} and avoid duplication, since the authority is the
     * same for every codes returned by this factory. It also make it easier for clients to
     * prepend whatever authority name they wish, as for example in the
     * {@linkplain org.geotoolkit.referencing.factory.AllAuthoritiesFactory#getAuthorityCodes
     * all authorities factory}.
     */
    @Override
    public Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        ensureInitialized();
        final Set<String> set = new LinkedHashSet<>();
        for (final Map.Entry<Integer,CoordinateReferenceSystem> entry : crsMap.entrySet()) {
            final CoordinateReferenceSystem crs = entry.getValue();
            if (type.isAssignableFrom(crs.getClass())) {
                final Integer code = entry.getKey();
                set.add(String.valueOf(code));
            }
        }
        return set;
    }

    /**
     * Returns the CRS name for the given code.
     *
     * @throws FactoryException if an error occurred while fetching the description.
     */
    @Override
    public InternationalString getDescriptionText(final String code) throws FactoryException {
        return new SimpleInternationalString(createObject(code).getName().getCode());
    }

    /**
     * Creates an object from the specified code. The default implementation delegates to
     * <code>{@linkplain #createCoordinateReferenceSystem createCoordinateReferenceSystem}(code)</code>.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public IdentifiedObject createObject(final String code) throws FactoryException {
        return createCoordinateReferenceSystem(code);
    }

    /**
     * Creates a coordinate reference system from the specified code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(final String code)
            throws FactoryException
    {
        String c = trimAuthority(code).toUpperCase();
        if (c.startsWith(PREFIX)) {
            /*
             * "trimAuthority" removed "CRS" when it was separated from the code, as in "CRS:84".
             * This block removes "CRS" when it is concatenated with the code, as in "CRS84".
             */
            c = c.substring(PREFIX.length());
        }
        final int i;
        try {
            i = Integer.parseInt(c);
        } catch (NumberFormatException exception) {
            // If a number can't be parsed, then this is an invalid authority code.
            NoSuchAuthorityCodeException e = noSuchAuthorityCode(CoordinateReferenceSystem.class, code);
            e.initCause(exception);
            throw e;
        }
        ensureInitialized();
        final CoordinateReferenceSystem crs = crsMap.get(i);
        if (crs != null) {
            return crs;
        }
        throw noSuchAuthorityCode(CoordinateReferenceSystem.class, code);
    }
}
