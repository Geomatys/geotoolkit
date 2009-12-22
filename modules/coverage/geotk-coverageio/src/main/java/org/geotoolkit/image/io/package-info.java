/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 * Base classes for extensions to {@link javax.imageio.ImageReader} and {@link javax.imageio.ImageWriter}
 * for spatial data. The base {@code ImageReader} class for reading raster data is
 * {@link org.geotoolkit.image.io.SpatialImageReader}, completed by a specialized {@code ImageReadParam}
 * class for parameters ({@link org.geotoolkit.image.io.SpatialImageReadParam}) and a specialized
 * {@code IIOMetadata} class for metadata ({@link org.geotoolkit.image.io.metadata.SpatialMetadata}).
 * <p>
 * Concrete implementations are provided in the {@linkplain org.geotoolkit.image.io.plugin plugin}
 * sub-package for binary image formats, and in the {@linkplain org.geotoolkit.image.io.text text}
 * sub-package for ASCII (or other text encoding) image formats.
 * <p>
 * The {@link org.geotoolkit.image.io.XImageIO} class provides static methods completing the ones
 * provided in the standard {@link javax.imageio.ImageIO} class. Those methods consider the input
 * or output type before to select an image reader or writer, because not every plugins can accept
 * the standard types (image input or output stream) defined by the Java Image I/O specification.
 *
 * {@section Conversion of sample values}
 * Spatial image formats often contain geophysical values (e.g. temperatures in Celsius degrees,
 * elevation in metres, etc.) better represented as floating point numbers than integers. Those
 * files may be simple ASCII files containing values written as decimal numbers, or RAW files
 * containing values written in the IEEE 754 binary format. Those files may contains <cite>missing
 * values</cite> represented by a "pad" or "fill" value. The {@code SpatialImageReader} class
 * provides {@link org.geotoolkit.image.io.SampleConverter} for:
 * <p>
 * <ul>
 *   <li>Replacing fill values by {@linkplain java.lang.Float#NaN NaN} if the destination image
 *       is backed by floating point values.</li>
 *   <li>Scaling the values in the range of the destination image type if the image is backed
 *       by some integer type. Such conversions are applied only if they are necessary for storing
 *       the data in the destination image.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.07
 *
 * @see org.geotoolkit.image.io.text
 * @see org.geotoolkit.image.io.plugin
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.image.io;
