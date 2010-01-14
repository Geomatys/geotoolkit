/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
 * Wrappers for referencing objects defined in external libraries. They allow to use the
 * external objects as implementations of GeoAPI interfaces. This package provides adapters
 * for the following libraries:
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th>Library</th>
 *     <th>Main external class</th>
 *     <th>Defined in module</th>
 *   </tr><tr>
 *     <td><a href="http://www.unidata.ucar.edu/software/netcdf-java">NetCDF</a></td>
 *     <td>{@link ucar.nc2.dataset.CoordinateSystem}</td>
 *     <td>{@code geotk-coverageio-netcdf}</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
package org.geotoolkit.referencing.adapters;
