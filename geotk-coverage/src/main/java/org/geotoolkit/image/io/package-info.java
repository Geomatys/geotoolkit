/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Base classes for extensions to {@link javax.imageio.ImageReader} and
 * {@link javax.imageio.ImageWriter} for spatial data. This package provides the
 * following classes which can be used as a base for plugin implementations:
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
 *   </tr><tr bgcolor="lightblue">
 *     <th>{@link javax.imageio.ImageReadParam}</th>
 *     <th>{@link javax.imageio.ImageWriteParam}</th>
 *     <th>Purpose</th>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.SpatialImageReadParam}</td>
 *     <td>{@link org.geotoolkit.image.io.SpatialImageWriteParam}</td>
 *     <td>Specializations of the standard {@link javax.imageio.IIOParam} class for
 *         multi-dimensional dataset and for specifying color palette.</td>
 *   </tr><tr bgcolor="lightblue">
 *     <th colspan="2">{@link javax.imageio.metadata.IIOMetadata}</th>
 *     <th>Purpose</th>
 *   </tr><tr>
 *     <td colspan="2">{@link org.geotoolkit.image.io.metadata.SpatialMetadata}</td>
 *     <td>Geographic metadata structured in an arborescence similar to ISO 19115-2.</td>
 *   </tr>
 * </table>
 * <p>
 * Concrete implementations are provided in the {@linkplain org.geotoolkit.image.io.plugin plugin}
 * and {@linkplain org.geotoolkit.image.io.mosaic mosaic} sub-packages.
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
 * <p>
 * The default sample type is {@link java.awt.image.DataBuffer#TYPE_FLOAT}. This default value is
 * a compromise between compactness and reducing the risk of information lost. However rendering
 * floating-point images is usually very slow. After reading, users can exploit
 * <a href="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</a> operations
 * in order to reformat data as needed. The example below reformats the
 * {@link java.awt.image.DataBuffer#TYPE_FLOAT TYPE_FLOAT} data into
 * {@link java.awt.image.DataBuffer#TYPE_BYTE TYPE_BYTE} and replaces the grayscale colors by an
 * indexed color model.
 *
 * {@preformat java
 *     import java.awt.RenderingHints;
 *     import java.awt.image.DataBuffer;
 *     import java.awt.image.IndexColorModel;
 *     import java.awt.image.renderable.ParameterBlock;
 *     import javax.media.jai.operator.FormatDescriptor;
 *     import javax.media.jai.operator.RescaleDescriptor;
 *
 *     public class Example {
 *         public static RenderedImage reformat(RenderedImage image) {
 *             // Prepare the indexed color model. Arrays
 *             // R, G and B should contains 256 RGB values.
 *             final byte[] R = ...
 *             final byte[] G = ...
 *             final byte[] B = ...
 *             final IndexColorModel colors = new IndexColorModel(8, 256, R, G, B);
 *             final ImageLayout     layout = new ImageLayout().setColorModel(colorModel);
 *             final RenderingHints   hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
 *
 *             // Rescale the image.   First, all pixels values are transformed using
 *             // the equation pi=CO+C1*p. Then, type float is clamp to type byte and
 *             // the new index color model is set.   Displaying such an image should
 *             // be much faster.
 *             final double C0 = ...
 *             final double C1 = ...
 *             image = RescaleDescriptor.create(image, new double[] {C1}, new double[] {C0}, null);
 *             image = FormatDescriptor .create(image, DataBuffer.TYPE_BYTE, null);
 *             return image;
 *         }
 *     }
 * }
 *
 * {@section Static utility methods}
 * The {@link org.geotoolkit.image.io.XImageIO} class provides static methods completing the ones
 * provided in the standard {@link javax.imageio.ImageIO} class. Those methods consider the input
 * or output type before to select an image reader or writer, because not every plugins can accept
 * the standard types (image input or output stream) defined by the Java Image I/O specification.
 * <p>
 * The {@link org.geotoolkit.coverage.io.CoverageIO} class provides higher-level static methods
 * related to {@link org.geotoolkit.coverage.grid.GridCoverage} I/O operations.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.20
 *
 * @see org.geotoolkit.image.io.plugin
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.image.io;
// For javadoc
