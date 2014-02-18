/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
 * A schema for storing coverages metadata in a SQL database. The coverage sample values are
 * stored as ordinary files encoded in arbitrary image formats (PNG, RAW, ASCII, JPEG-2000,
 * <i>etc.</i> - note that the classic JPEG format is not recommended).
 *
 * A <A HREF="http://www.postgresql.org/">PostgreSQL</A> database is used for storing coverage
 * <em>metadata</em> like geographic envelopes and meaning of pixel values. The database is also
 * used as an index for searching image files from a 2D, 3D or 4D spatio-temporal envelopes.
 *
 * {@section Installation}
 * The current implementation requires the PostgreSQL database with the PostGIS extension.
 * The easiest way to install the database is to run the
 * <a href="{@docRoot}/../modules/display/geotk-wizards-swing/CoverageDatabaseInstaller.html">graphical wizard</a>.
 * The database can also be created manually by running the SQL scripts available on the
 * <a href="http://hg.geotoolkit.org/geotoolkit/file/tip/modules/coverage/geotk-coverage-sql/src/main/resources/org/geotoolkit/coverage/sql">Mercurial
 * repository</a>.
 *
 * {@section Connection}
 * The connection to the database is specified by a JDBC {@link javax.sql.DataSource}
 * and a {@link java.util.Properties} map, which are given to the
 * {@link org.geotoolkit.coverage.sql.CoverageDatabase} constructor.
 * The properties map can contain the following optional entries:
 *
 * <blockquote><table border="1" cellspacing="0" cellpadding="2">
 *   <tr bgcolor="lightblue"><th>Key</th><th>Usage</th></tr>
 *   <tr><td>{@code URL}</td>           <td>&nbsp;The JDBC URL to the database (only if no {@link javax.sql.DataSource} is given).</td></tr>
 *   <tr><td>{@code user}</td>          <td>&nbsp;The user name.</td></tr>
 *   <tr><td>{@code password}</td>      <td>&nbsp;The password.</td></tr>
 *   <tr><td>{@code schema}</td>        <td>&nbsp;The database schema to use. The default is to use the PostgreSQL {@code "search_path"} variable.</td></tr>
 *   <tr><td>{@code timezone}</td>      <td>&nbsp;The timezone for the dates in the database. Default is UTC.</td></tr>
 *   <tr><td>{@code rootDirectory}</td> <td>&nbsp;The root directory of image files. Paths declared in the database are relative to that directory.</td></tr>
 * </table></blockquote>
 *
 * {@section Usage}
 * A {@link org.geotoolkit.coverage.sql.CoverageDatabase} can be used in different ways.
 * Some methods can be invoked directly on the {@code CoverageDatabase} instance:
 *
 * {@preformat java
 *     CoverageDatabase db = new CoverageDatabase(...);
 *     CoverageQuery query = new CoverageQuery(db);
 *     CoverageEnvelope env = query.getEnvelope();
 *     env.setHorizontalRange(...); // Convenience method for setting the horizontal dimension.
 *     env.setTimeRange(...);       // Convenience method for setting the temporal dimension.
 *     query.setEnvelope(env);      // As a matter of principle, but actually not necessary.
 *     query.setLayer("My Layer");
 *     FutureQuery<GridCoverage2D> fc = db.readSlice(query);
 *
 *     // Do some other work here while the coverage is loaded in background.
 *
 *     GridCoverage2D coverage = fc.result();
 * }
 *
 * It is also possible to get a {@link org.geotoolkit.coverage.sql.LayerCoverageReader}
 * instance, which give access to the same functionalities through the
 * {@link org.geotoolkit.coverage.io.GridCoverageReader} API.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
package org.geotoolkit.coverage.sql;
