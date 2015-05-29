/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Implementations of {@linkplain org.geotoolkit.coverage.processing.AbstractOperation operations}.
 * This package is useful for documentation purpose since class names match exactly operation
 * names, and the javadoc for each class provides a table of valid arguments. But the classes
 * provided there should not be used directly. There is no need to instantiate them, since it
 * is already done by the {@linkplain org.geotoolkit.coverage.processing.DefaultCoverageProcessor
 * default coverage processor} which manage them.
 * <p>
 * If the operation to apply is know at compile time, then the easiest way to use this package
 * is to use the {@link org.geotoolkit.coverage.processing.Operations} convenience class. For
 * example a {@linkplain org.opengis.coverage.grid.GridCoverage grid coverage} can be resampled
 * to a different {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate
 * reference system} using the following code:
 *
 * {@preformat java
 *     Coverage reprojected = Operations.DEFAULT.resample(myCoverage, newCRS);
 * }
 *
 * If the operation to apply is unknown at compile time (for example because it is
 * selected at runtime by the user in some widget), or if the operation is not listed
 * in the {@code Operations} convenience class, then the generic way is to invoke the
 * {@link org.geotoolkit.coverage.processing.AbstractCoverageProcessor#doOperation doOperation} method
 * on the {@linkplain org.geotoolkit.coverage.processing.AbstractCoverageProcessor#getInstance default
 * processor instance}. Available operations are listed in this package, but the tables below
 * summarize the main ones.
 * <p>
 * <H1><A NAME="operation-list"><U>Supported operations</U></A></H1>
 * <UL>
 *   <LI><A HREF="#Convolve">Convolve</A></LI>
 *   <LI><A HREF="#GradientMagnitude">GradientMagnitude</A></LI>
 *   <LI><A HREF="#Invert">Invert</A></LI>
 *   <LI><A HREF="#LaplaceType1Filter">LaplaceType1Filter</A></LI>
 *   <LI><A HREF="#LaplaceType2Filter">LaplaceType2Filter</A></LI>
 *   <LI><A HREF="#MaxFilter">MaxFilter</A></LI>
 *   <LI><A HREF="#MedianFilter">MedianFilter</A></LI>
 *   <LI><A HREF="#MinFilter">MinFilter</A></LI>
 *   <LI><A HREF="#Recolor">Recolor</A></LI>
 *   <LI><A HREF="#Threshold">Threshold</A></LI>
 *  </UL>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="Convolve">Convolve</A></H2>
 * <P>Computes each output sample by multiplying elements
 * of a kernel with the samples surrounding a particular source sample.</P>
 * <P><b>Name:</b>&nbsp;{@code "Convolve"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.ConvolveDescriptor Convolve}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <TABLE border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "kernel"}</td>
 *   <td>{@link javax.media.jai.KernelJAI}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></TABLE>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="LaplaceType1Filter">LaplaceType1Filter</A></H2>
 * <P>Perform a laplacian filter operation on a grid coverage. This is a high pass filter which
 * highlights the edges having positive and negative brightness slopes. This filter mulitples the
 * co-efficients in the tabe below with the corresponding grid data value in the kernel window.
 * The new grid value will be calculated as the sum of <code>(grid value * co-efficient)</code>
 * for each kernel cell divised by 9.</P>
 * <TABLE border='1' cellpadding='6'>
 *   <TR align="center"> <TD>0</TD>  <TD>-1</TD>   <TD>0</TD></TR>
 *   <TR align="center"><TD>-1</TD>   <TD>4</TD>  <TD>-1</TD></TR>
 *   <TR align="center"> <TD>0</TD>  <TD>-1</TD>   <TD>0</TD></TR>
 * </TABLE>
 * <P><b>Name:</b>&nbsp;{@code "LaplaceType1Filter"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.ConvolveDescriptor Convolve}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 *   <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="LaplaceType2Filter">LaplaceType2Filter</A></H2>
 * <P>Perform a laplacian filter operation on a grid coverage. This is a high pass filter which
 * highlights the edges having positive and negative brightness slopes. This filter mulyiplies
 * the co-efficients in the tabe below with the corresponding grid data value in the kernel window.
 * The new grid value will be calculated as the sum of <code>(grid value * co-efficient)</code>
 * for each kernel cell divised by 9.</P>
 * <TABLE border='1' cellpadding='6'>
 *   <TR align="center"><TD>-1</TD>  <TD>-1</TD>  <TD>-1</TD></TR>
 *   <TR align="center"><TD>-1</TD>   <TD>8</TD>  <TD>-1</TD></TR>
 *   <TR align="center"><TD>-1</TD>  <TD>-1</TD>  <TD>-1</TD></TR>
 * </TABLE>
 * <P><b>Name:</b>&nbsp;{@code "LaplaceType1Filter"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.ConvolveDescriptor Convolve}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="MaxFilter">MaxFilter</A></H2>
 * <P>Non-linear filter which is useful for removing isolated lines or pixels while preserving the
 * overall appearance of an image. The filter is implemented by moving a mask over the image. For
 * each position of the mask, the center pixel is replaced by the max of the pixel values covered
 * by the mask. There are several shapes possible for the mask, which are enumerated in the
 * {@linkplain javax.media.jai.operator.MaxFilterDescriptor JAI documentation}.</P>
 * <P><b>Name:</b>&nbsp;{@code "MaxFilter"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.MaxFilterDescriptor MaxFilter}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Xsize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Ysize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "maskShape"}</td>
 *   <td>{@link javax.media.jai.operator.MaxFilterShape}</td>
 *   <td>{@link javax.media.jai.operator.MaxFilterDescriptor#MAX_MASK_SQUARE}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><b>Note:</b> In current implementation, {@code Xsize} and {@code Ysize}
 * must have the same value (i.e. rectangular shapes are not supported).</P>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="MedianFilter">MedianFilter</A></H2>
 * <P>Non-linear filter which is useful for removing isolated lines or pixels while preserving the
 * overall appearance of an image. The filter is implemented by moving a mask over the image. For
 * each position of the mask, the center pixel is replaced by the median of the pixel values
 * covered by the mask. This filter results in a smoothing of the image values.
 * There are several shapes possible for the mask, which are enumerated in the
 * {@linkplain javax.media.jai.operator.MedianFilterDescriptor JAI documentation}.</P>
 * <P><b>Name:</b>&nbsp;{@code "MedianFilter"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.MedianFilterDescriptor MedianFilter}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Xsize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Ysize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "maskShape"}</td>
 *   <td>{@link javax.media.jai.operator.MedianFilterShape}</td>
 *   <td>{@link javax.media.jai.operator.MedianFilterDescriptor#MEDIAN_MASK_SQUARE}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><b>Note:</b> In current implementation, {@code Xsize} and {@code Ysize} must
 * have the same value (i.e. rectangular shapes are not supported).</P>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="MinFilter">MinFilter</A></H2>
 * <P>Non-linear filter which is useful for removing isolated lines or pixels while preserving
 * the overall appearance of an image. The filter is implemented by moving a mask over the image.
 * For each position of the mask, the center pixel is replaced by the min of the pixel values
 * covered by the mask. There are several shapes possible for the mask, which are enumerated in
 * the {@linkplain javax.media.jai.operator.MinFilterDescriptor JAI documentation}.</P>
 * <P><b>Name:</b>&nbsp;{@code "MinFilter"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.MinFilterDescriptor MinFilter}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Xsize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "Ysize"}</td>
 *   <td>{@link java.lang.Integer}</td>
 *   <td align="center">3</td>
 *   <td align="center">1</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "maskShape"}</td>
 *   <td>{@link javax.media.jai.operator.MinFilterShape}</td>
 *   <td>{@link javax.media.jai.operator.MinFilterDescriptor#MIN_MASK_SQUARE}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><b>Note:</b> In current implementation, {@code Xsize} and {@code Ysize} must
 * have the same value (i.e. rectangular shapes are not supported).</P>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="Recolor">Recolor</A></H2>
 * <P>Changes the colors associated to arbitrary {@linkplain org.geotoolkit.coverage.Category categories}
 * in arbitrary bands. The {@code ColorMaps} arguments must be an array of {@link java.util.Map}s
 * with a minimal length of 1. The {@code Map} in array element 0 is used
 * for band 0; the {@code Map} in array element 1 is used for band 1, etc.
 * If there is more bands than array elements in {@code ColorMaps}, then
 * the last {@code Map} is reused for all remaining bands.</P>

 * <P>For each {@link java.util.Map} in {@code ColorMaps}, the keys are category names as
 * {@link java.lang.String} and the values are colors as an array of type
 * <code>{@linkplain java.awt.Color}[]</code>. All categories with a name matching a key in
 * the {@code Map} will be {@linkplain org.geotoolkit.coverage.Category#recolor recolored} with
 * the associated colors. All categories with no corresponding entries in the {@code Map}
 * will be left unchanged. The {@code null} key is a special value meaning "any quantitative
 * category". For example in order to repaint forest in green, river in blue and lets other
 * categories unchanged, one can write:</P>

 * {@preformat java
 *     Map map = new HashMap();
 *     map.put("Forest", new Color[]{Color.GREEN});
 *     map.put("River",  new Color[]{Color.BLUE });
 *     Map[] colorMaps = new Map[] {
 *         map  // Use for all bands
 *     }
 * }
 *
 * <P><b>Name:</b>&nbsp;{@code "Recolor"}<BR>
 * <b>JAI operator:</b>&nbsp;N/A<BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "ColorMaps"}</td>
 *   <td><code>{@linkplain java.util.Map}[]</code></td>
 *   <td align="center">A gray scale</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * <P>&nbsp;</P>
 * <HR>
 * <P>&nbsp;</P>
 * <H2><A NAME="Threshold">Threshold</A></H2>
 * <P>A gray scale threshold classifies the grid coverage values into a boolean value. The sample
 * dimensions will be modified into a boolean value and the dimension type of the source sample
 * dimension will be represented as 1 bit.</P>
 * <P><b>Name:</b>&nbsp;{@code "Threshold"}<BR>
 * <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.BinarizeDescriptor Binarize}"</CODE><BR>
 * <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
 * <tr bgcolor='#B9DCFF'>
 *   <th>Name</th>
 *   <th>Class</th>
 *   <th>Default value</th>
 *   <th>Minimum value</th>
 *   <th>Maximum value</th>
 * </tr><tr>
 *   <td>{@code "Source"}</td>
 *   <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr><tr>
 *   <td>{@code "threshold"}</td>
 *   <td>{@code double[]}</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 *   <td align="center">N/A</td>
 * </tr></table>
 * <P><A HREF="#operation-list">Back to summary</A></P>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.coverage.processing.operation;
