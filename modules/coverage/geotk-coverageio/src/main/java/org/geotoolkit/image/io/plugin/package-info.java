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
 * Extensions to {@link javax.imageio.ImageReader} and {@link javax.imageio.ImageWriter} for binary
 * files and text files. The {@code geotk-coverageio.jar} and {@code geotk-coverageio-netcdf.jar}
 * files declare service providers for the following formats:
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th>{@link javax.imageio.ImageReader} subclass</th>
 *     <th>{@link javax.imageio.ImageWriter} subclass</th>
 *     <th>Name</th>
 *     <th>MIME type</th>
 *     <th>Comments</th>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.AsciiGridReader}&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.AsciiGridWriter}&nbsp;</td>
 *     <td>&nbsp;{@code "ascii-grid"}&nbsp;</td>
 *     <td>&nbsp;text/plain&nbsp;</td>
 *     <td>&nbsp;US locale and ASCII encoding.&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.NetcdfImageReader}&nbsp;</td>
 *     <td>&nbsp;&nbsp;&nbsp;</td>
 *     <td>&nbsp;{@code "netcdf"}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;Assume <A HREF="http://www.cfconventions.org/">CF Metadata conventions</A>.&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.RawImageReader}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;{@code "raw"}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;Require {@link com.sun.media.imageio.stream.RawImageInputStream}.&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.TextMatrixImageReader}&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.TextMatrixImageWriter}&nbsp;</td>
 *     <td>&nbsp;{@code "matrix"}&nbsp;</td>
 *     <td>&nbsp;text/plain&nbsp;</td>
 *     <td>&nbsp;Locale sensitive.&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.TextRecordImageReader}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;{@code "records"}&nbsp;</td>
 *     <td>&nbsp;text/plain&nbsp;</td>
 *     <td>&nbsp;Locale sensitive.&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageReader}&nbsp;</td>
 *     <td>&nbsp;{@link org.geotoolkit.image.io.plugin.WorldFileImageWriter}&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;Require {@linkplain org.geotoolkit.image.io.plugin.WorldFileImageReader.Spi#registerDefaults
 *         explicit registration}.&nbsp;</td>
 *   </tr>
 * </table>
 *
 * {@section Note about text formats}
 * In the case of text files, the {@link java.nio.charset.Charset} used for decoding/encoding
 * streams and the {@link java.util.Locale} used for parsing/formatting numbers are plugin-specific.
 * Some plugins use the {@linkplain java.util.Locale#getDefault() system default}, which make them
 * locale-dependent (see the table above). However it is possible to derive a locale-insensitive
 * format from a locale sensitive one. See the
 * {@link org.geotoolkit.image.io.plugin.TextMatrixImageReader.Spi} javadoc for an example.
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
 * @author Antoine Hnawia (IRD)
 * @version 3.08
 *
 * @since 3.07 (derived from 1.2)
 * @module
 */
package org.geotoolkit.image.io.plugin;
