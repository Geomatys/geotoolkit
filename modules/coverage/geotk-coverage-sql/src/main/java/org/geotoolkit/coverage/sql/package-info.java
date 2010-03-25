/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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
 * A schema for storing coverages metadata in a SQL database. The connection to the database
 * is specified by a JDBC {@link javax.sql.DataSource} and a {@link java.util.Properties} map,
 * which are given to the {@link org.geotoolkit.coverage.sql.CoverageDatabase} constructor.
 * The properties map can contain the following optional entries:
 * <p>
 * <table>
 *   <tr><td>{@code user}</td>          <td>The user name.</td></tr>
 *   <tr><td>{@code password}</td>      <td>The password.</td></tr>
 *   <tr><td>{@code schema}</td>        <td>The database schema to use.</td></tr>
 *   <tr><td>{@code timezone}</td>      <td>The timezone for the dates in the database. Default is UTC.</td></tr>
 *   <tr><td>{@code rootDirectory}</td> <td>The root directory of image files. Paths declared
 *   in the database are relative to that directory.</td></tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
package org.geotoolkit.coverage.sql;
