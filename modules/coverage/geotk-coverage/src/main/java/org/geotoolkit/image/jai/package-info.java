/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
 * A set of <A HREF="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</A>
 * operations. All operations defined here work on J2SE's {@linkplain java.awt.image.RenderedImage
 * rendered images}; there is no OpenGIS's {@linkplain org.opengis.coverage.grid.GridCoverage grid
 * coverage} dependencies here. Examples.
* <p>
 * <table align="center" cellpadding="15" border="1">
 * <tr><th>Operation</th><th>Input</th><th>output</th></tr>
 * <tr>
 *   <td valign="center">{@link org.geotoolkit.image.jai.SilhouetteMask}</td>
 *   <td><img src="doc-files/sample-rgb.png" border="1"></td>
 *   <td><img src="doc-files/silhouette.png" border="1"></td>
 * </tr><tr>
 *   <td valign="center">{@link org.geotoolkit.image.jai.Mask}</td>
 *   <td><img src="doc-files/sample-rgb.png" border="1"> ,
 *       <img src="doc-files/silhouette.png" border="1"></td>
 *   <td><img src="doc-files/mask.png" border="1"></td>
 * </tr></table>
 *
 *
 * {@section Registration}
 *
 * The JAI operations provided in this package should be registered automatically at JAI startup time,
 * since they are declared in the {@code META-INF/registryFile.jai} file. However, this default JAI
 * mechanism may fail in some occasions, for example when the Geotk JAR file is unreachable from
 * the JAI class loader. In such case, the {@link org.geotoolkit.image.jai.Registry#registerGeotoolkitServices
 * registerGeotoolkitServices} method may be invoked programmatically as a fallback. This is done
 * automatically by the {@link org.geotoolkit.coverage.processing} package; users need to care only
 * if they want to use directly the custom JAI operations provided in this package.
 *
 * @author Lionel Flahaut (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.01
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.image.jai;
