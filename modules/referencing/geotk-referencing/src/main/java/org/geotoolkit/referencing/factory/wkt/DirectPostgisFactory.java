/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.wkt;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.resources.Vocabulary;


/**
 * An authority factory creating CRS from the {@value #TABLE} table in a spatial
 * SQL database. This class is called <code>Direct<u>Postgis</u>Factory</code> because
 * of some assumptions more suitable to PostGIS, like the default {@linkplain #getAuthority()
 * authority} if none were explicitly defined. But this class should be usable with other OGC
 * compliant spatial database as well.
 * <p>
 * This factory doesn't cache any result. Any call to a {@code createFoo} method will
 * trig a new WKT parsing. For adding caching service, this factory needs to be wrapped
 * in a {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory} instance. The
 * {@link AuthorityFactoryProvider#createFromPostGIS AuthorityFactoryProvider}
 * convenience class can be used for that purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.5)
 * @module
 */
public class DirectPostgisFactory extends WKTParsingAuthorityFactory implements CRSAuthorityFactory {
    /**
     * The standard name of the table containing CRS definitions, which is {@value}.
     */
    public static final String TABLE = "spatial_ref_sys";

    /**
     * The primary key column, which is {@value}.
     */
    public static final String PRIMARY_KEY = "srid";

    /**
     * The standard name ({@value}) of the column containing the authority names.
     */
    public static final String AUTHORITY_COLUMN = "auth_name";

    /**
     * The standard name ({@value}) of the column containing the authority codes.
     */
    public static final String CODE_COLUMN = "auth_srid";

    /**
     * The standard name ({@value}) of the column containing the WKT definitions.
     */
    public static final String WKT_COLUMN = "srtext";

    /**
     * Authorities found in the database. Will be computed only when first needed.
     * Keys are authority names, and values are whatever the authority codes match
     * primary keys or not.
     */
    private transient Map<String,Boolean> authorityUsePK;

    /**
     * Creates a factory using the given connection. The connection is
     * {@linkplain Connection#close() closed} when this factory is
     * {@linkplain #dispose(boolean) disposed}.
     * <p>
     * <b>Note:</b> we recommend to avoid keeping the connection open for a long time. An easy
     * way to get the connection created only when first needed and closed automatically after
     * a short timeout is to instantiate this {@code DirectPostgisFactory} class only in a
     * {@link org.geotoolkit.referencing.factory.ThreadedAuthorityFactory}. This approach also
     * gives concurrency and caching services in bonus.
     *
     * @param hints The hints, or {@code null} if none.
     * @param connection The connection to the database.
     * @throws SQLException If an error occurred while fetching metadata from the database.
     */
    public DirectPostgisFactory(final Hints hints, final Connection connection) throws SQLException {
        super(hints, new SpatialRefSysMap(connection));
    }

    /**
     * Returns a description of the underlying backing store.
     */
    @Override
    public String getBackingStoreDescription() throws FactoryException {
        final Citation   authority = getAuthority();
        final TableWriter    table = new TableWriter(null, " ");
        final Vocabulary resources = Vocabulary.getResources(null);
        CharSequence cs;
        if ((cs=authority.getEdition()) != null) {
            final String identifier = Citations.getIdentifier(authority);
            table.write(resources.getString(Vocabulary.Keys.VERSION_OF_$1, identifier));
            table.write(':');
            table.nextColumn();
            table.write(cs.toString());
            table.nextLine();
        }
        try {
            String s;
            final DatabaseMetaData metadata = ((SpatialRefSysMap) definitions).connection.getMetaData();
            if ((s=metadata.getDatabaseProductName()) != null) {
                table.write(resources.getLabel(Vocabulary.Keys.DATABASE_ENGINE));
                table.nextColumn();
                table.write(s);
                if ((s = metadata.getDatabaseProductVersion()) != null) {
                    table.write(' ');
                    table.write(resources.getString(Vocabulary.Keys.VERSION_$1, s));
                }
                table.nextLine();
            }
            if ((s = metadata.getURL()) != null) {
                table.write(resources.getLabel(Vocabulary.Keys.DATABASE_URL));
                table.nextColumn();
                table.write(s);
                table.nextLine();
            }
        } catch (SQLException exception) {
            throw databaseFailure(null, null, exception);
        }
        return table.toString();
    }

    /**
     * Returns the authority which is responsible for the maintenance of the primary keys.
     * Note that <cite>primary keys</cite> are not necessarily the same than authority codes.
     * The primary keys are stored in the {@value #PRIMARY_KEY} column, while the authority
     * codes are defined by the {@value #AUTHORITY_COLUMN} : {@value #CODE_COLUMN} tuples.
     * <p>
     * The default implementation returns {@link Citations#POSTGIS} in all cases.
     *
     * @return The authority which is responsible for the maintenance of primary keys.
     *
     * @see #getPrimaryKey(Class, String)
     */
    @Override
    public Citation getPrimaryKeyAuthority() {
        return Citations.POSTGIS;
    }

    /**
     * Returns all authority names declared in the CRS table. If some authorities use the same
     * codes than the primary key, then those authorities are returned first, ordered by the
     * most common ones. The last element in the array is the authority returned by
     * {@link #getPrimaryKeyAuthority()}.
     *
     * @return All authorities found in the database.
     */
    @Override
    final synchronized Citation[] getAuthorities() {
        Citation[] authorities = this.authorities;
        if (authorities == null) {
            final Citation pkAuthority = getPrimaryKeyAuthority();
            int count = 0;
            try {
                final Set<String> names = getAuthorityNames().keySet();
                authorities = new Citation[names.size() + 1];
                for (final String name : names) {
                    final Citation authority = (name != null) ? Citations.fromName(name) : pkAuthority;
                    /*
                     * If the authority is not one of the predefined constants (in which case the
                     * class would have been CitationConstant), then add the name to the list of
                     * identifiers.
                     */
                    if (authority.getClass() == DefaultCitation.class) {
                        ((DefaultCitation) authority).getIdentifiers().add(new DefaultIdentifier(name));
                    }
                    authorities[count++] = authority;
                }
            } catch (FactoryException exception) {
                Logging.unexpectedException(LOGGER, DirectPostgisFactory.class, "getAuthority", exception);
                authorities = new Citation[] {getPrimaryKeyAuthority()};
            }
            authorities[count] = pkAuthority;
            this.authorities = authorities;
        }
        return authorities;
    }

    /**
     * Returns the authority names found in the database. Keys are authority names,
     * and values are whatever the authority codes match primary keys or not.
     *
     * @return All authority names found in the database.
     * @throws FactoryException if an access to the database failed.
     */
    private Map<String,Boolean> getAuthorityNames() throws FactoryException {
        assert Thread.holdsLock(this);
        if (authorityUsePK == null) {
            try {
                authorityUsePK = ((SpatialRefSysMap) definitions).getAuthorityNames();
            } catch (SQLException exception) {
                throw databaseFailure(null, null, exception);
            }
        }
        return authorityUsePK;
    }

    /**
     * Returns the authority codes defined in the database for the given type.
     *
     * @param category The type of objects to search for (typically
     *        <code>{@linkplain CoordinateReferenceSystem}.class</code>).
     * @return The set of available codes.
     * @throws FactoryException if an error occurred while querying the database.
     */
    @Override
    public synchronized Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> category)
            throws FactoryException
    {
        try {
            return ((SpatialRefSysMap) definitions).getAuthorityCodes(category);
        } catch (SQLException exception) {
            throw databaseFailure(category, null, exception);
        }
    }

    /**
     * Returns the primary key for the specified authority code. If the supplied code contains an
     * <cite>authority</cite> part as in {@code "EPSG:4326"}, then this method searches for a row
     * with the given authority ({@code "EPSG"}) in the {@value #AUTHORITY_COLUMN} column and the
     * given integer code ({@code 4326}) in the {@value #CODE_COLUMN} column. If such row is found,
     * then the value of its {@value #PRIMARY_KEY} column is returned.
     * <p>
     * If the supplied code does not contain an <cite>authority</cite> part (e.g. {@code "4326"}),
     * then this method parses the code as an integer. This is consistent with common practice
     * where the spatial CRS table contains entries from a single authority with primary keys
     * identical to the authority codes. This is also consistent with the codes returned by the
     * {@link #getAuthorityCodes(Class)} method.
     *
     * @param  type The type of the object being created (usually
     *         <code>{@linkplain CoordinateReferenceSystem}.class</code>).
     * @param  code The authority code to convert to primary key value.
     * @return The primary key for the supplied code. There is no guarantee that this key exists
     *         (this method may or may not query the database).
     * @throws NoSuchAuthorityCodeException if a code can't be parsed as an integer or can't
     *         be found in the database.
     * @throws FactoryException if an error occurred while querying the database.
     *
     * @see #getPrimaryKeyAuthority()
     */
    @Override
    public synchronized Integer getPrimaryKey(final Class<? extends IdentifiedObject> type, String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        code = code.trim();
        final int separator = code.lastIndexOf(DefaultNameSpace.DEFAULT_SEPARATOR);
        final String authority  = (separator >= 0) ? code.substring(0, separator).trim() : "";
        final String identifier = code.substring(separator+1).trim();
        int srid;
        try {
            srid = Integer.parseInt(identifier);
        } catch (NumberFormatException cause) {
            NoSuchAuthorityCodeException e = noSuchAuthorityCode(IdentifiedObject.class, code);
            e.initCause(cause);
            throw e;
        }
        if (authority.isEmpty() || Boolean.TRUE.equals(getAuthorityNames().get(authority))) {
            return srid;
        }
        final Integer c;
        try {
            c = ((SpatialRefSysMap) definitions).getPrimaryKey(code, authority, srid);
        } catch (SQLException exception) {
            throw databaseFailure(type, code, exception);
        }
        if (c == null) {
            throw noSuchAuthorityCode(type, code);
        }
        return c;
    }

    /**
     * Closes the JDBC connection used by this factory.
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        try {
            ((SpatialRefSysMap) definitions).dispose();
        } catch (SQLException exception) {
            Logging.unexpectedException(DirectPostgisFactory.class, "dispose", exception);
        }
        authority   = null;
        authorities = null;
        super.dispose(shutdown);
    }
}
