/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
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
 * Basic geometric objects. The {@link org.geotoolkit.geometry.GeneralDirectPosition} class represents
 * a point in a multi-dimensional space, and {@link org.geotoolkit.geometry.GeneralEnvelope} represents
 * a box in a multi-dimensional space. This space may have an arbitrary number of dimensions.
 * <p>
 * In the particular case of two-dimensional space, {@code DirectPosition} is conceptually
 * equivalent to {@link java.awt.geom.Point2D} and {@code Envelope} is conceptually equivalent
 * to {@link java.awt.geom.Rectangle2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.geometry;
