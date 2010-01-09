/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2010, Open Source Geospatial Foundation (OSGeo)
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
 * Measures (like {@linkplain org.geotoolkit.measure.Angle angles}) and their formatters.
 * This package defines:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.measure.Angle} and its subclasses
 *      ({@link org.geotoolkit.measure.Longitude},
 *       {@link org.geotoolkit.measure.Latitude})</li>
 *   <li>Formatters
 *      ({@link org.geotoolkit.measure.AngleFormat},
 *       {@link org.geotoolkit.measure.CoordinateFormat},
 *       {@link org.geotoolkit.measure.RangeFormat})</li>
 * </ul>
 *
 * {@note <code>MeasurementRange</code> is a class closely related to measurements, but
 *        defined in the <code>org.geotoolkit.util</code> package for consistency with
 *        the <code>Range</code> base class. However its formatter is defined in this
 *        package for consistency with other measurement-related formatters.}
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.06
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.measure;
