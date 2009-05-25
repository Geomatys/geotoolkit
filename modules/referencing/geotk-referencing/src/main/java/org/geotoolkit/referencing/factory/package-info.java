/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.opengis.referencing.Factory Factories} and base classes for
 * {@linkplain org.opengis.referencing.AuthorityFactory authority factories}.
 *
 * {@section Authority factories}
 * The authority factories can be divised in three categories:
 *
 * <ul>
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.DirectAuthorityFactory}
 *       do the real work of creating Coordinate Reference System objects from authority
 *       codes. They differ in the way their data are stored:
 *       {@link org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory} uses a connection
 *       to an EPSG database, {@link org.geotoolkit.referencing.factory.DirectPostgisFactory}
 *       uses a connection to a PostGIS {@code "spatial_ref_sys"} table and
 *       {@link org.geotoolkit.referencing.factory.PropertyAuthorityFactory} uses static
 *       WKT strings in a property file.</p></li>
 *
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory}
 *       wrap the above {@code DirectAuthorityFactories} and cache their results, which lead
 *       to significant performance improvement. They may also use more than one direct
 *       authority factory instance for concurrency in multi-thread environment. Subclasses
 *       include {@link org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory} which wraps a
 *       {@code DirectEpsgFactory} and {@link org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory}
 *       which wraps a {@code PropertyAuthorityFactory}.</p></li>
 *
 *   <li><p>Subclasses of {@link org.geotoolkit.referencing.factory.AuthorityFactoryAdapter}
 *       wraps the above {@code CachingAuthorityFactories} and apply some changes on the result
 *       in order to adapt them to a different usage context. The most common adaptation is to
 *       force the axis order to longitude first, which can be seen as an adaptation of
 *       <cite>Web Map Service</cite> (WMS) 1.3 objects to WMS 1.0 objects.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.referencing.factory;
