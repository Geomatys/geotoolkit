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
 * Extensions to {@link javax.imageio.ImageReader} and {@link javax.imageio.ImageWriter} for
 * binary files. The <code>geotk-coverageio.jar</code> file declares service providers for the
 * following formats:
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th>{@link javax.imageio.ImageReader} subclass</th>
 *     <th>{@link javax.imageio.ImageWriter} subclass</th>
 *     <th>Name</th>
 *     <th>MIME type</th>
 *     <th>Comments</th>
 *   </tr><tr>
 *     <td>{@link org.geotoolkit.image.io.plugin.RawImageReader}</td>
 *     <td>&nbsp;</td>
 *     <td>{@code "raw"}</td>
 *     <td>&nbsp;</td>
 *     <td>&nbsp;Requires {@link com.sun.media.imageio.stream.RawImageInputStream}.&nbsp;</td>
 *   </tr>
 * </table>
 * <p>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see org.geotoolkit.image.io.text
 *
 * @since 3.07
 * @module
 */
package org.geotoolkit.image.io.plugin;
