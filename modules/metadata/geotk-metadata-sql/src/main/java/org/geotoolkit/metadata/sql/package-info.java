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
 * An implementation of {@linkplain org.opengis.metadata.Metadata Metadata} interfaces
 * fetching the data from an SQL database. Each metadata classes are mapped to a table,
 * and each metadata attributes are mapped to a column in the appropriate table. Tables
 * and columns are created only when first needed.
 * <p>
 * This package is not a replacement for more sophisticated metadata applications like
 * <a href="http://www.mdweb-project.org/">MD-Web</a>. This package provides only a direct
 * mapping (i.e. no meta-model) of metadata <cite>interfaces</cite> and <cite>methods</cite>
 * to database <cite>tables</cite> and <cite>columns</cite> with limited capability. This is
 * suitable only for applications wanting a simple metadata schema. The restrictions are:
 * <p>
 * <ul>
 *   <li>Interfaces and methods must have {@link org.opengis.annotation.UML} annotations.</li>
 *   <li>Collections are not currently supported (only the first element is stored).</li>
 *   <li>{@link org.opengis.util.InternationalString} are stored only for the default locale.</li>
 *   <li>Cyclic graph (<var>A</var> references <var>B</var> which reference <var>A</var>) are not
 *       supported, unless foreigner key constraints are manually disabled for the columns which
 *       contain the cyclic references.</li>
 *   <li>Metadata that are sub-interface of other metadata (for example
 *       {@link org.opengis.metadata.extent.GeographicDescription} which extends
 *       {@link org.opengis.metadata.extent.GeographicExtent}) can be stored only
 *       in databases supporting <cite>table inheritance</cite>, like
 *       <a href="http://www.postgresql.org">PostgreSQL</a>.</li>
 * </ul>
 *
 * @author Touraïvane (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.1)
 * @module
 */
package org.geotoolkit.metadata.sql;
