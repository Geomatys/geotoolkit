/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * Performs conversions between instances of different classes. For example it is sometime
 * convenient to consider {@link java.util.Date} objects as if they were {@link java.lang.Long}
 * objects for computational purpose in generic algorithms. This particular conversion is
 * straightforward when the type are known at compile-time, but more tedious when they must
 * be determined dynamically at run-time. This package allow that in a two-steps process:
 * <p>
 * <ul>
 *   <li>Get a converter from {@link org.geotoolkit.util.converter.ConverterRegistry} from
 *       a given <var>source</var> to a given <var>target</var> class.</li>
 *   <li>Use the obtained {@link org.geotoolkit.util.converter.ObjectConverter} instance
 *       for performing the conversions.</li>
 * </ul>
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
package org.geotoolkit.util.converter;

