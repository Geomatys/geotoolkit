/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
 * Extensions to {@link javax.imageio.ImageReader} for binary and ASCII files.
 * {@link org.geotoolkit.image.io.StreamImageReader} is the base class for image decoders reading
 * stream with few (if any) meta-data. Examples of such streams are matrix containing the pixels
 * values in a binary form (RAW images), or ASCII files containing values written as decimal
 * numbers. Such files contain often geophysical values (e.g. temperature in Celsius degrees,
 * elevation in metres, etc.) better represented as floating point numbers than integers.
 * <p>
 * By default, {@link org.geotoolkit.image.io.StreamImageReader} stores decoded image using data
 * type {@link java.awt.image.DataBuffer#TYPE_FLOAT} and a grayscale color space. This politic
 * produces images matching closely the original data, i.e. it involves as few transformations
 * as possible. But displaying floating-point images is usually very slow. Users are strongly
 * encouraged to use <a href="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</a>
 * operations after reading in order to scale data as they see fit. The example below reformats the
 * {@link java.awt.image.DataBuffer#TYPE_FLOAT} data into {@link java.awt.image.DataBuffer#TYPE_BYTE}
 * and changes the grayscale colors to an indexed color model.
 *
 * {@preformat java
 *     import java.awt.RenderingHints;
 *     import java.awt.image.DataBuffer;
 *     import java.awt.image.IndexColorModel;
 *     import java.awt.image.renderable.ParameterBlock;
 *     import javax.media.jai.operator.FormatDescriptor;
 *     import javax.media.jai.operator.RescaleDescriptor;
 *
 *     // Omitting class and method declaration...
 *
 *     // Prepare the indexed color model. Arrays
 *     // R, G and B should contains 256 RGB values.
 *     final byte[] R = ...
 *     final byte[] G = ...
 *     final byte[] B = ...
 *     final IndexColorModel colors = new IndexColorModel(8, 256, R, G, B);
 *     final ImageLayout     layout = new ImageLayout().setColorModel(colorModel);
 *     final RenderingHints   hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
 *
 *     // Rescale the image.   First, all pixels values are transformed using
 *     // the equation pi=CO+C1*p. Then, type float is clamp to type byte and
 *     // the new index color model is set.   Displaying such an image should
 *     // be much faster.
 *    final double C0 = ...
 *    final double C1 = ...
 *    image = RescaleDescriptor.create(image, new double[] {C1}, new double[] {C0}, null);
 *    image = FormatDescriptor .create(image, DataBuffer.TYPE_BYTE, null);
 * }
 *
 * @author Martin Desruisseaux (IRD)
 * @author Antoine Hnawia (IRD)
 * @version 3.0
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.image.io;
