/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *     <td>&nbsp;{@link org.apache.sis.geometry.GeneralEnvelope}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.geometry.Envelope2D}&nbsp;</td>
 *     <td>&nbsp;{@link java.awt.geom.Rectangle2D}&nbsp;</td>
 *   </tr>
 * </table>
 *
 * {@section Envelopes spanning the anti-meridian of a Geographic CRS}
 * The Web Coverage Service (WCS) 1.1 specification uses an extended interpretation of the bounding
 * box definition. In a WCS 1.1 data structure, the {@linkplain org.opengis.geometry.Envelope#getLowerCorner()
 * lower corner} defines the edges region in the directions of <em>decreasing</em> coordinate values
 * in the {@linkplain org.opengis.geometry.Envelope#getCoordinateReferenceSystem() envelope CRS},
 * while the {@linkplain org.opengis.geometry.Envelope#getUpperCorner() upper corner} defines the
 * edges region in the directions of <em>increasing</em> coordinate values. Those lower and upper
 * corners are usually the algebraic {@linkplain org.opengis.geometry.Envelope#getMinimum(int) minimum}
 * and {@linkplain org.opengis.geometry.Envelope#getMaximum(int) maximum} coordinates respectively,
 * but not always. For example, an envelope crossing the anti-meridian could have a lower corner
 * longitude greater than the upper corner longitude, like the red box below (the green box is the
 * usual case):
 *
 * <center><img src="doc-files/AntiMeridian.png"></center>
 *
 * As of Geotk 3.20, every envelopes defined in this package support the extended bounding box
 * interpretation: for any dimension, ordinate values such that <var>upper</var> &lt; <var>lower</var>
 * are handled in a special way. This handling is slightly different for two groups of methods:
 * <p>
 * <ul>
 *   <li>In calculation of envelopes spans and median positions (centers) - handled specially only
 *       on axes having the {@link org.opengis.referencing.cs.RangeMeaning#WRAPAROUND WRAPAROUND}
 *       range meaning</li>
 *   <li>When checking for containment, intersections or unions - can be handled specially for
 *       any axis, in which case the envelope represents an <em>exclusion</em> area instead
 *       than an inclusion area.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.geometry;
