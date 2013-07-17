/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
 * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> parsing and formatting. This package provides the internal mechanism
 * used by Geotk implementation. Most users don't need to know about it, except if they want to
 * customize the WKT output. For example it is possible to:
 * <p>
 * <ul>
 *   <li>{@linkplain org.geotoolkit.io.wkt.WKTFormat#setConvention Format the parameters using the
 *       names of an other authority than OGC}. For example we may want to format using the
 *       {@linkplain org.geotoolkit.io.wkt.Convention#GEOTIFF GeoTIFF} parameter names.</li>
 *   <li>{@linkplain org.geotoolkit.io.wkt.WKTFormat#setSymbols Use curly brackets instead than
 *       square ones}, as in {@code DATUM("WGS84")} instead than {@code DATUM["WGS84"]}. This is
 *       legal WKT, while less frequently used than square brackets.</li>
 *   <li>{@linkplain org.geotoolkit.io.wkt.WKTFormat#setColors Apply syntactic coloring} for output
 *       on X3.64 terminal.</li>
 *   <li>{@linkplain org.geotoolkit.io.wkt.WKTFormat#setIndentation Use a different indentation}, or
 *       format the whole WKT on a {@linkplain org.geotoolkit.io.wkt.WKTFormat#SINGLE_LINE single line}.</li>
 * </ul>
 * <p>
 * Current implementation is primarily targeting the parsing and formatting of referencing objects.
 * However other WKT formats (especially the one for geometric objects) are expected to be
 * provided here in future versions.
 *
 * {@section Referencing WKT}
 * Parsing of {@link org.opengis.referencing.crs.CoordinateReferenceSystem} and
 * {@link org.opengis.referencing.operation.MathTransform} objects are performed
 * by the {@link org.geotoolkit.io.wkt.ReferencingParser} class. The parser provides
 * methods for:
 * <p>
 * <ul>
 *   <li>Specifying whatever the default axis names shall be ISO identifiers or the
 *       legacy identifiers specified in the WKT specification.</li>
 *   <li>Ignoring the {@code AXIS[...]} elements. This approach can be used as a way to force
 *       the (<var>longitude</var>, <var>latitude</var>) axes order.</li>
 * </ul>
 *
 * {@section Geometry WKT}
 * The {@link org.apache.sis.geometry.GeneralEnvelope} and
 * {@link org.geotoolkit.geometry.GeneralDirectPosition} classes provide their own, limited,
 * WKT parsing and formatting services for the {@code BOX} and {@code POINT} elements.
 *
 * {@section References}
 * <ul>
 *   <li><A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well Know Text specification</A></li>
 *   <li><A HREF="http://home.gdal.org/projects/opengis/wktproblems.html">OGC WKT Coordinate System Issues</A></li>
 *   <li><A HREF="http://en.wikipedia.org/wiki/Well-known_text">Well Known Text in Wikipedia</A></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Eve (IRD)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.io.wkt;
