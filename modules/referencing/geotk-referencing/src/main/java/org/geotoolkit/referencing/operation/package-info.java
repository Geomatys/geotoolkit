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
 * {@linkplain org.geotoolkit.referencing.operation.AbstractCoordinateOperation Coordinate operation} implementations.
 * An explanation for this package is provided in the {@linkplain org.opengis.referencing.operation OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * This package provides an ISO 19111 {@link org.geotoolkit.referencing.operation.AbstractCoordinateOperation
 * Coordinate Operation implementation} and support classes. The actual transform work is performed by the
 * following sub-packages, but most users will not need to deal with them directly:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection} for map projections</li>
 *   <li>{@link org.geotoolkit.referencing.operation.transform} for any transform other than map projections</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider} for registering the transforms from the above 2 packages.</li>
 * </ul>
 * <p>
 * In order to reduce the need to explore those low-level sub-packages, this package defines a
 * {@link org.geotoolkit.referencing.operation.MathTransforms} class which centralize in one
 * places some of the most frequently used functions from the sub-packages.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.referencing.operation;
