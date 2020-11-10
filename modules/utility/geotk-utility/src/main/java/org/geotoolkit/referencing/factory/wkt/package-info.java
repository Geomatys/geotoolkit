/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

/**
 * {@linkplain org.opengis.referencing.AuthorityFactory Authority factories} for CRS parsed from
 * <cite><a href="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well
 * Known Text</a></cite> (WKT). This package contains two main implementations:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.factory.wkt.PropertyAuthorityFactory}
 *       for parsing CRS from a {@linkplain java.util.Properties property file}.</li>
 *   <li>{@link org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory} for parsing CRS
 *       from the {@value org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory#TABLE}
 *       table of a PostGIS database:.</li>
 * </ul>
 * <p>
 * The public implementations provided in this package does not cache the CRS - each call to a
 * {@code createFoo} method will trig a new WKT parsing.
 * <p>
 * By default, the classes defined in this package are <strong>not</strong> registered for use
 * though the {@link org.geotoolkit.factory.FactoryFinder}. Users shall either instantiate and
 * use a class directly, or create a subclass and
 * <a href="../../../factory/package-summary.html#package_description">register</a> it themselves.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.1)
 * @module
 */
package org.geotoolkit.referencing.factory.wkt;
