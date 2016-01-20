/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.opengis.referencing.AuthorityFactory Authority factories} for the
 * <A HREF="http://www.epsg.org">EPSG</A> database. Every classes in this package except
 * {@link org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory} require a connection
 * to a database, which may be on Derby (a.k.a. JavaDB), HSQL, PostgreSQL or MS-Access.
 *
 *
 * {@section Installation}
 *
 * See <a href="doc-files/install.html">installation instructions</a>.
 *
 *
 * {@section Fetching a connection}
 *
 * By default, this package fetches a connection to a database in the {@code Geotoolkit.org/EPSG}
 * directory (relative to the user home directory). This database will be automatically created
 * if the {@code geotk-epsg} module is available on the classpath. The automatic installation and
 * usage will work only if one of the {@code derby.jar} or {@code hsql.jar} driver is available on
 * the classpath.
 * <p>
 * The connection to the database can also be specified explicitly in a simple properties file or
 * through JNDI. The steps used for fetching the connection parameters are described in the javadoc
 * of the {@link org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory#getDataSource()} method.
 *
 *
 * {@section Getting a factory instance}
 *
 * An EPSG authority factory is created using the following code:
 *
 * {@preformat java
 *     CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Yann Cézard (IRD)
 * @author Rueben Schulz (UBC)
 * @author Matthias Basler
 * @author Andrea Aime (TOPP)
 * @author Jody Garnett (Refractions)
 * @author Didier Richard (IGN)
 * @author John Grange
 *
 * @deprecated Moved to {@link org.apache.sis.referencing.factory.sql}.
 */
package org.geotoolkit.referencing.factory.epsg;
