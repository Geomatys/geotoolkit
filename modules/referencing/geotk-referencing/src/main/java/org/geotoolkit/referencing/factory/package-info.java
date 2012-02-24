/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.opengis.util.Factory Factories} and base classes for
 * {@linkplain org.opengis.referencing.AuthorityFactory authority factories}.
 *
 * {@section Authority factories}
 * The authority factories can be divided in three categories:
 *
 * <ul>
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.DirectAuthorityFactory}
 *       do the real work of creating Coordinate Reference System objects from authority
 *       codes. They differ in the way their data are stored:
 *       <ul>
 *         <li>{@link org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory} uses a connection
 *             to an EPSG database,</li>
 *         <li>{@link org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory} uses a
 *             connection to a PostGIS {@code "spatial_ref_sys"} table, and</li>
 *         <li>{@link org.geotoolkit.referencing.factory.wkt.PropertyAuthorityFactory} uses static
 *             WKT strings in a property file.</li>
 *       </ul></p></li>
 *
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory}
 *       wrap the above {@code DirectAuthorityFactories} and cache their results, which lead
 *       to significant performance improvement. They may also use more than one direct
 *       authority factory instance for concurrency in multi-thread environment. Subclasses
 *       include:
 *       <ul>
 *         <li>{@link org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory}
 *             which wraps a {@code DirectEpsgFactory} and</li>
 *         <li>{@link org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory}
 *              which wraps a {@code PropertyAuthorityFactory}.</li>
 *       </ul></p></li>
 *
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.AuthorityFactoryAdapter}
 *       wraps the above {@code CachingAuthorityFactories} and apply some changes on the result
 *       in order to adapt them to a different usage context. The most common adaptation is to
 *       force the axis order to longitude first, which can be seen as an adaptation of
 *       <cite>Web Map Service</cite> (WMS) 1.3 objects to WMS 1.0 objects.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.referencing.factory;
