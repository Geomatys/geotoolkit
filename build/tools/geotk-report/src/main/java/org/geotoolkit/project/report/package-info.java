/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
 * Generates some HTML reports for the Geotk library. The classes in this package iterate
 * over some kind of services declared by the Geotk library (for example the list of map
 * projections) and produce HTML reports to be copied on the web server. The reports are
 * for example the list of all supported EPSG codes.
 *
 * <p>This package is used for the following reports:</p>
 *
 * <ul>
 *   <li><a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">Authority codes for
 *   Coordinate Reference Systems</a> created by {@link org.geotoolkit.project.report.CRSAuthorityCodes}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.16)
 */
package org.geotoolkit.project.report;
