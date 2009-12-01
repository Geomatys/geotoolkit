/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
 * Extensions to {@link javax.imageio.ImageReader} and {@link javax.imageio.ImageWriter} for
 * ASCII files. The {@link java.nio.charset.Charset} used for decoding/encoding streams, and
 * the {@link java.util.Locale} used for parsing/formatting numbers, are plugin-specific. But
 * some plugins use the {@linkplain java.util.Locale#getDefault() system default} unless otherwise
 * specified, which make them locale-dependant (see the table below).
 * <p>
 * The <code>geotk-coverageio.jar</code> file declares service providers for the following formats:
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th>{@link javax.imageio.ImageReader} subclass</th>
 *     <th>{@link javax.imageio.ImageWriter} subclass</th>
 *     <th>Name</th>
 *     <th>MIME type</th>
 *     <th>Comments</th>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.text.AsciiGridReader}</td>
 *     <td>{@link org.geotoolkit.image.io.text.AsciiGridWriter}</td>
 *     <td>{@code "ascii-grid"}</td>
 *     <td>text/plain</td>
 *     <td>US locale and ASCII encoding</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.text.TextMatrixImageReader}</td>
 *     <td>{@link org.geotoolkit.image.io.text.TextMatrixImageWriter}</td>
 *     <td>{@code "matrix"}</td>
 *     <td>text/plain</td>
 *     <td>Locale sensitive</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.text.TextRecordImageReader}</td>
 *     <td>&nbsp;</td>
 *     <td>{@code "records"}</td>
 *     <td>text/plain</td>
 *     <td>Locale sensitive</td>
 *   </tr>
 * </table>
 * <p>
 * <b>Example:</b> The text of the left side is an extract of a list of
 * (<var>longitude</var>, <var>latitude</var>, <var>elevation of the ocean floor</var>)
 * records. The image on the right side is the image produced by
 * {@link org.geotoolkit.image.io.text.TextRecordImageReader} when reading such file.
 *
 * <table cellpadding='24'>
 * <tr valign="top"><td><pre>
 * Longitude Latitude Altitude
 * 59.9000   -30.0000   -3022
 * 59.9333   -30.0000   -3194
 * 59.9667   -30.0000   -3888
 * 60.0000   -30.0000   -3888
 * 45.0000   -29.9667   -2502
 * 45.0333   -29.9667   -2502
 * 45.0667   -29.9667   -2576
 * 45.1000   -29.9667   -2576
 * 45.1333   -29.9667   -2624
 * 45.1667   -29.9667   -2690
 * 45.2000   -29.9667   -2690
 * 45.2333   -29.9667   -2692
 * 45.2667   -29.9667   -2606
 * 45.3000   -29.9667   -2606
 * 45.3333   -29.9667   -2528</pre>etc...</td>
 * <td><img src="doc-files/Sandwell.jpeg"></td>
 * </tr></table>
 *
 * {@section Reformating to integer sample values}
 * By default, {@link org.geotoolkit.image.io.StreamImageReader} creates images backed by floating
 * point values ({@link java.awt.image.DataBuffer#TYPE_FLOAT}) and using a grayscale color space.
 * This politic produces images matching closely the original data, involving as few transformations
 * as possible. However displaying floating-point images is usually very slow. Users are strongly
 * encouraged to use <a href="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</a>
 * operations after reading in order to scale data as they see fit. The example below reformats the
 * {@link java.awt.image.DataBuffer#TYPE_FLOAT TYPE_FLOAT} data into
 * {@link java.awt.image.DataBuffer#TYPE_BYTE TYPE_BYTE} and changes the grayscale colors to an
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
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.06
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.image.io.text;
