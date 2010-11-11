/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;


/**
 * Interface for objects that can be formatted as <cite>Well Known Text</cite> (WKT).
 * This interface provides a {@link #formatWKT(Formatter)} method, which is invoked
 * at WKT formatting time.
 * <p>
 * Except for {@link org.geotoolkit.referencing.operation.transform.AffineTransform2D},
 * most Geotk implementations extend {@link FormattableObject} rather than implementing
 * directly this interface.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public interface Formattable {
    /**
     * Formats the inner part of a <cite>Well Known Text</cite> (WKT) element. This method is
     * automatically invoked by {@link Formatter#append(Formattable)}. Element name and authority
     * code must not be formatted here. For example for a {@code GEOGCS} element, the formatter
     * will invoke this method for completing the WKT at the insertion point show below:
     *
     * {@preformat text
     *     GEOGCS["WGS 84", AUTHORITY["EPSG","4326"]]
     *                    ↑
     *            (insertion point)
     * }
     *
     * @param  formatter The formatter to use.
     * @return The name of the WKT element type (e.g. {@code "GEOGCS"}).
     */
    String formatWKT(Formatter formatter);
}
