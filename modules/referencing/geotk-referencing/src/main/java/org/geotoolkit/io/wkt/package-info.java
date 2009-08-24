/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> parsing and formatting. This package provides the internal mechanism
 * used by Geotoolkit implementation. Most users don't need to know about it, except if they want to
 * customize the WKT output. For example it is possible to:
 * <p>
 * <ul>
 *   <li>format parameter using the names of an other authority than OGC. For example we may want
 *       to format using the GeoTIFF parameter names.</li>
 *   <li>Use curly brackets instead than square ones, as in {@code DATUM("WGS84")} instead than
 *       {@code DATUM["WGS84"]}. This is legal WKT, even if less frequent than square brackets.</li>
 *   <li>Apply syntatic coloring for output on X3.64 terminal</li>
 * </ul>
 * <p>
 * Current implementation is primarily targeting parsing and formatting of referencing objects.
 * However other WKT formats (especially the one for geometric objects) are expected to be
 * provided here in future versions.
 *
 * {@section References}
 * <ul>
 *   <li><A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well Know Text specification</A></li>
 *   <li><A HREF="http://home.gdal.org/projects/opengis/wktproblems.html">OGC WKT Coordinate System Issues</A></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @author Rémi Eve (IRD)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.io.wkt;
