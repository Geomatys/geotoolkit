/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Support for drawable axis with graduation. Axis objects ({@link org.geotoolkit.display.axis.Axis2D})
 * are lines ({@link java.awt.geom.Line2D}) with a graduation ({@link org.geotoolkit.display.axis.Graduation}).
 * Axes are graduated from the starting point ({@link java.awt.geom.Line2D#getP1()}) to the end
 * point ({@link java.awt.geom.Line2D#getP2()}). Since axes can have arbitrary starting and end
 * points (usually expressed in pixel coordinates), axes can be located anywhere in a widget and
 * have any direction: vertical, horizontal, inclined, increasing up or down, <i>etc.</i>
 * Two axes don't have to be perpendicular. As long as they are not parallel, it is always possible
 * to construct an {@link java.awt.geom.AffineTransform} mapping logical values to pixel coordinates
 * on any arbitrary axis, no matter their orientation.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.display.axis;
