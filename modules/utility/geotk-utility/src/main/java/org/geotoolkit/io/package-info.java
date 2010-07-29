/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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
 * Extensions to standard Java {@link java.io.Reader} and {@link java.io.Writer} for I/O operations.
 * Contains also a few {@link java.text.Format} implementations that are expected to be used in
 * close relationship with readers or writers.
 * <p>
 * Many {@code Writers} defined in this package are actually {@link java.io.FilterWriter}
 * used for applying on-the-fly formatting while writing text to the output device. For
 * example {@link org.geotoolkit.io.IndentedLineWriter} adds intendation at the beginning
 * of every new line, and {@link org.geotoolkit.io.TableWriter} replaces all occurence of
 * {@code '\t'} by the amount of spaces needed for producing a tabular output.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 1.0
 * @module
 */
package org.geotoolkit.io;
