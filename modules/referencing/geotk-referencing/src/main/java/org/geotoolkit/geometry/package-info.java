/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * Basic geometric objects. Every geometry objects are associated with a
 * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System},
 * which may have an arbitrary number of dimensions. However a few specialized classes restrict
 * the CRS to a fixed number of dimensions only. The table below summarizes the most common
 * objects, and list the Java2D classes that are conceptually equivalent.
 * <p>
 * <table border="1" cellspacing="0" cellpadding="4">
 *   <tr bgcolor="lightblue">
 *     <th>Purpose</th>
 *     <th>Any dimension</th>
 *     <th>One dimension</th>
 *     <th>Two dimensions</th>
 *     <th>Java2D equivalence</th>
 *   </tr><tr>
 *     <td>&nbsp;A point in a multi-dimensional space&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.GeneralDirectPosition}&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.DirectPosition1D}&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.DirectPosition2D}&nbsp;</td>
 *     <td>&nbsp;{@link java.awt.geom.Point2D}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;A box in a multi-dimensional space&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.GeneralEnvelope}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.Envelope2D}&nbsp;</td>
 *     <td>&nbsp;{@link java.awt.geom.Rectangle2D}&nbsp;</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.geometry;
