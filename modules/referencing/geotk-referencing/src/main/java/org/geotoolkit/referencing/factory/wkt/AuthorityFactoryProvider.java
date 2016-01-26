/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.wkt;

import java.net.URL;
import java.io.IOException;
import javax.sql.DataSource;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;


/**
 * Convenience class for creating {@link CRSAuthorityFactory} instances using custom CRS definitions.
 * Most methods in this class expect the {@linkplain CoordinateReferenceSystem Coordinate Reference
 * System} (CRS) definitions to be provided in
 * <cite><a href="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well
 * Known Text</a></cite> (WKT) form, since this is often the easiest way to define custom CRS.
 * <p>
 * The following example creates a factory for ESRI codes. The CRS are defined in a
 * {@linkplain java.util.Properties properties} file in the same directory than the
 * class invoking the method (replace {@code MyClass} by the name of your class):
 *
 * {@preformat java
 *     URL definitionURL = MyClass.class.getResource("myfile.properties");
 *     AuthorityFactoryProvider provider = new AuthorityFactoryProvider();
 *     CRSAuthorityFactory factory = provider.createFromProperties(Citations.ESRI, definitionURL);
 * }
 *
 * The following example creates a factory which use the definitions provided in the
 * {@value org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory#TABLE} table
 * of a PostGIS database:
 *
 * {@preformat java
 *     final PGSimpleDataSource ds = new PGSimpleDataSource();
 *     ds.setServerName("myServer");
 *     ds.setDatabaseName("myDatabase");
 *     ds.setUser("myUsername");
 *     ds.setPassword("myPassword");
 *     AuthorityFactoryProvider provider = new AuthorityFactoryProvider();
 *     CRSAuthorityFactory factory = provider.createFromPostGIS(ds);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public class AuthorityFactoryProvider {
    /**
     * The user hints, or {@code null} if none.
     */
    private final Hints hints;

    /**
     * Creates a new instance which will use the default hints.
     */
    public AuthorityFactoryProvider() {
        hints = null;
    }

    /**
     * Creates a new instance which will use the given hints.
     *
     * @param hints The user hints, or {@code null} if none.
     */
    public AuthorityFactoryProvider(final Hints hints) {
        this.hints = (hints != null) ? hints.clone() : null;
    }

    /**
     * Creates a factory for the specified authority using the definitions declared in the given
     * {@linkplain java.util.Properties properties} file. The property file is specified by an
     * {@link URL}, which is typically obtained by invoking {@link Class#getResource(String)}.
     *
     * @param  authority The organization or party responsible for CRS definition.
     * @param  definitionFile URL to the definition file.
     * @return A new authority factory backed by the given authority file.
     * @throws FactoryException If the authority factory can not be created, typically because
     *         the given definition file was not found.
     */
    public CRSAuthorityFactory createFromProperties(final Citation authority, final URL definitionFile)
            throws FactoryException
    {
        final PropertyAuthorityFactory factory;
        try {
            factory = new PropertyAuthorityFactory(hints, definitionFile, authority);
        } catch (IOException e) {
            throw new FactoryException(e);
        }
        return factory;
    }

    /**
     * Creates a factory for the specified PostGIS database. The factory returned by this
     * method implements the {@link org.geotoolkit.util.Disposable} interface. Invoking its
     * {@code dispose()} method closes any JDBC connection which may be open.
     *
     * @param  datasource Provides connection to the PostGIS database.
     * @return A new authority factory backed by the given PostGIS database.
     * @throws FactoryException If the authority factory can not be created.
     */
    public CRSAuthorityFactory createFromPostGIS(final DataSource datasource) throws FactoryException {
        return new CachingPostgisFactory(datasource);
    }
}
