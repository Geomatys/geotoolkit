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
 * Extensions to {@link javax.imageio.ImageReader} for ASCII files. The <code>geotk-coverageio.jar</code>
 * file declares a service provider for the following image readers:
 * <p>
 * <table align="center" border="3" cellpadding="6" bgcolor="F4F8FF">
 *   <tr bgcolor="#B9DCFF">
 *     <th>{@link javax.imageio.ImageReader} subclass</th>
 *     <th>Name</th>
 *     <th>MIME type</th>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.RawBinaryImageReader}</td>
 *     <td>raw</td>
 *     <td>image/raw</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.text.TextMatrixImageReader}</td>
 *     <td>matrix</td>
 *     <td>text/matrix</td>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.text.TextRecordImageReader}</td>
 *     <td>gridded records</td>
 *     <td>text/x-grid</td>
 *   </tr>
 * </table>
 * <p>
 * <b>Example:</b> a user may want to read an ASCII file containing gridded elevation on the
 * ocean floor (left side below). The {@link org.geotoolkit.image.io.text.TextRecordImageReader}
 * class can read such file, detect automatically minimum and maximum values (in order to scale
 * the grayscale palette) and produce the image below:
 * <p>
 * <table align="center" cellpadding='24'>
 * <tr><td><pre>Longitude Latitude Altitude
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
 * 45.3333   -29.9667   -2528
 * </pre>etc...</td>
 * <td><img src="doc-files/Sandwell.jpeg"></td>
 * </tr></table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.image.io.text;
