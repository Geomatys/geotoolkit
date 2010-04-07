/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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
 * Base classes for extensions to {@link javax.imageio.ImageReader} and
 * {@link javax.imageio.ImageWriter} for spatial data. This package provides the
 * following abstract classes which can be used as a base for plugin implementations:
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th>{@link javax.imageio.ImageReader}</th>
 *     <th>{@link javax.imageio.ImageWriter}</th>
 *     <th>Purpose</th>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.SpatialImageReader}</td>
 *     <td>{@link org.geotoolkit.image.io.SpatialImageWriter}</td>
 *     <td>Base class for readers/writers of spatial (usually geographic) data.</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.StreamImageReader}</td>
 *     <td>{@link org.geotoolkit.image.io.StreamImageWriter}</td>
 *     <td>Base class for readers/writers working with
 *         {@link java.io.InputStream}/{@link java.io.OutputStream} or channels.
 *         Other kind of input/output are converted to stream when first needed.</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.TextImageReader}</td>
 *     <td>{@link org.geotoolkit.image.io.TextImageWriter}</td>
 *     <td>Base class for readers/writers working with {@link java.io.Reader}/{@link java.io.Writer}.
 *         This implies the use of a character encoding, which may be local-dependent.</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.FileImageReader}</td>
 *     <td>&nbsp;</td>
 *     <td>Base class for readers/writers that require {@link java.io.File} input or output.
 *         Other kind of input/output are copied in a temporary file. This is used for wrapping
 *         native libraries which doesn't work with Java streams.</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.ImageReaderAdapter}</td>
 *     <td>{@link org.geotoolkit.image.io.ImageWriterAdapter}</td>
 *     <td>Base class for readers/writers which delegate most of their work to an other
 *         reader/writer. This is used for appending additional metadata to the ones
 *         processed by the standard readers/writers.</td>
 *   </tr>
 * </table>
 * <p>
 * Those abstract classes are completed by the following support classes:
 * <ul>
 *   <li>{@link org.geotoolkit.image.io.SpatialImageReadParam} and
 *     {@link org.geotoolkit.image.io.SpatialImageWriteParam}, which are
 *     specializations of the standard {@link javax.imageio.IIOParam} class.</li>
 *   <li>{@link org.geotoolkit.image.io.metadata.SpatialMetadata}, which is
 *     a specialization of the standard {@link javax.imageio.metadata.IIOMetadata}.</li>
 * </ul>
 * <p>
 * Concrete implementations are provided in the {@linkplain org.geotoolkit.image.io.plugin plugin}
 * and {@linkplain org.geotoolkit.image.io.mosaic mosaic} sub-packages.
 *
 * {@section Static utility methods}
 * The {@link org.geotoolkit.image.io.XImageIO} class provides static methods completing the ones
 * provided in the standard {@link javax.imageio.ImageIO} class. Those methods consider the input
 * or output type before to select an image reader or writer, because not every plugins can accept
 * the standard types (image input or output stream) defined by the Java Image I/O specification.
 *
 * {@section System initialization}
 * While not mandatory, it is recommanded to invoke the following methods at least once before
 * to use the Geotk library. Those methods are not invoked automatically in order to let users
 * control their application configuration.
 * <p>
 * <ol>
 *   <li>Invoke some AWT method first; see {@code setDefaultCodecPreferences()} below for explanation.</li>
 *   <li>{@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}</li>
 *   <li>{@link org.geotoolkit.image.io.plugin.WorldFileImageReader.Spi#registerDefaults(ServiceRegistry)}</li>
 *   <li>{@link org.geotoolkit.image.io.plugin.WorldFileImageWriter.Spi#registerDefaults(ServiceRegistry)}</li>
 * </ol>
 * <ul>
 *   <li><b>Alternative:</b> {@link org.geotoolkit.lang.Setup#initialize(Properties)} performs
 *       (among other tasks) all the above tasks except 1.</li>
 * </ul>
 * <p>
 * Those methods can be invoked more than once if the set of standard readers available (PNG, TIFF,
 * <i>etc.</i>) is changed. For example invoking {@code WorldFileImageReader.Spi.registerDefaults(...)}
 * again will replace the old <cite>World File</cite> readers by new one wrapping the new standard
 * readers.
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
 * @version 3.11
 *
 * @see org.geotoolkit.image.io.plugin
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.image.io;

import java.util.Properties;              // For javadoc
import javax.imageio.spi.ServiceRegistry; // For javadoc
