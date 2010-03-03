/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.Map;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.DatumAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.lang.ThreadSafe;


/**
 * A CRS Authority Factory that manages object creation by parsing <cite>Well Known Text</cite>
 * (WKT) strings. The strings may be loaded from property files or be queried in a database (for
 * example the {@code "spatial_ref_sys"} table in a PostGIS database).
 * <p>
 * This base implementation expects a map of (<var>code</var>, <var>WKT</var>) entries, where the
 * authority codes are the keys and WKT strings are the values. If the map is backed by a store
 * which may throw checked exceptions (for example a connection to a PostGIS database), then it
 * shall wrap the checked exceptions in {@link BackingStoreException}s.
 *
 * {@section Declaring more than one authority}
 * There is usually only one authority for a given instance of {@code WKTParsingAuthorityFactory},
 * but more authorities can be given to the constructor if the CRS objects to create should have
 * more than one {@linkplain CoordinateReferenceSystem#getIdentifiers identifier}, each with the
 * same code but different namespace. For example a
 * {@linkplain org.geotoolkit.referencing.factory.epsg.EsriExtension factory for CRS defined
 * by ESRI} uses the {@code "ESRI"} namespace, but also the {@code "EPSG"} namespace because
 * those CRS are used as extension of the EPSG database. Consequently the same CRS can be
 * identified as both {@code "ESRI:53001"} and {@code "EPSG:53001"}, where {@code "53001"}
 * is a unused code in the official EPSG database.
 *
 * {@section Caching of CRS objects}
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory should
 * be wrapped in {@link CachingAuthorityFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.geotoolkit.referencing.factory.wkt} package.
 */
@Deprecated
@ThreadSafe(concurrent = false)
public class WKTParsingAuthorityFactory extends org.geotoolkit.referencing.factory.wkt.WKTParsingAuthorityFactory
        implements CRSAuthorityFactory, CSAuthorityFactory, DatumAuthorityFactory
{
    /**
     * Creates a factory for the specified authorities using the definitions in the given map.
     * There is usually only one authority, but more can be given when the objects to create
     * should have more than one {@linkplain CoordinateReferenceSystem#getIdentifiers identifier},
     * each with the same code but different namespace. See the <a href="#skip-navbar_top">class
     * javadoc</a> for more details.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param definitions
     *          The object definitions as a map with authority codes as keys and WKT strings as values.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     */
    public WKTParsingAuthorityFactory(final Hints userHints, final Map<String,String> definitions,
            final Citation... authorities)
    {
        super(userHints, definitions, authorities);
    }
}
