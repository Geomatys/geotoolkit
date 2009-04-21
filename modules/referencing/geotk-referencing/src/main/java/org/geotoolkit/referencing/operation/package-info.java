/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
 * The remaining discussion on this page is specific to the Geotoolkit implementation.
 *
 * {@section Registering math transforms}
 * The {@linkplain org.geotoolkit.referencing.operation.DefaultMathTransformFactory math transform factory}
 * search for all math transforms in the class path, not just Geotoolkit implementations. To be found, math
 * transforms must be registered as services in its JAR file, more specifically in the following JAR entry:
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.referencing.operation.MathTransformProvider
 * }
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.referencing.operation;
