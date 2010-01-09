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

/**
 * {@linkplain org.opengis.referencing.AuthorityFactory Authority factories} for the CRS
 * specified in the <cite>Web Map Service</cite> specification (ISO 19128). This include:
 * <p>
 * <ul>
 * <li>{@code CRS} space</li>
 * <li>{@code AUTO2} space - <cite>automatic projections</cite> (dynamic projections)
 *     based on code and location:
 *     <ul>
 *       <li>{@code AUTO2} projection codes are in the range 42000-42499</li>
 *       <li><var>lon0</var> and <var>lat0</var> are central point of the projection</li>
 *       <li>The <var>lon0</var>/<var>lat0</var> are provided by the SRS parameter of the map request.</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * Those CRS are defined in Annex B (<cite>CRS definitions</cite>) of
 * ISO 19128 (<cite>Web Map Service Implementation Specification</cite>).
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
package org.geotoolkit.referencing.factory.web;
