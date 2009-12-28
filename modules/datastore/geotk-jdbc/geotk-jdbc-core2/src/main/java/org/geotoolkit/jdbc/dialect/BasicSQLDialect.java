/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc.dialect;

import java.io.IOException;
import java.io.StringWriter;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.opengis.filter.expression.Literal;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.jdbc.JDBCDataStore;


public abstract class BasicSQLDialect extends AbstractSQLDialect {

    protected BasicSQLDialect(final JDBCDataStore dataStore) {
        super(dataStore);
    }

    /**
     * Encodes a value in an sql statement.
     * <p>
     * Subclasses may wish to override or extend this method to handle specific
     * types. This default implementation does the following:
     * <ol>
     *   <li>The <tt>value</tt> is encoded via its {@link #toString()} representation.
     *   <li>If <tt>type</tt> is a character type (extends {@link CharSequence}),
     *   it is wrapped in single quotes (').
     * </ol>
     * </p>
     *
     */
    public void encodeValue(final Object value, final Class type, final StringBuilder sql) {

        //turn the value into a literal and use FilterToSQL to encode it
        final Literal literal = dataStore.getFilterFactory().literal(value);
        final FilterToSQL filterToSQL = dataStore.createFilterToSQL(null);

        final StringWriter w = new StringWriter();
        filterToSQL.setWriter(w);

        filterToSQL.visit(literal,type);

        sql.append(w.getBuffer().toString());
    }

    /**
     * Encodes a geometry value in an sql statement.
     * <p>
     * An implementations should serialize <tt>value</tt> into some exchange
     * format which will then be transported to the underlying database. For
     * example, consider an implementation which converts a geometry into its
     * well known text representation:
     * <pre>
     *   <code>
     *   sql.append( "GeomFromText('" );
     *   sql.append( new WKTWriter().write( value ) );
     *   sql.append( ")" );
     *   </code>
     *  </pre>
     * </p>
     * <p>
     *  The <tt>srid</tt> parameter is the spatial reference system identifier
     *  of the geometry, or 0 if not known.
     * </p>
     */
    public abstract void encodeGeometryValue(final Geometry value, final int srid, final StringBuilder sql)
        throws IOException;

    /**
     * Creates the filter encoder to be used by the datastore when encoding
     * query predicates.
     * <p>
     * Sublcasses can override this method to return a subclass of {@link FilterToSQL}
     * if need be.
     * </p>
     */
    public FilterToSQL createFilterToSQL() {
        final FilterToSQL f2s = new FilterToSQL();
        f2s.setCapabilities(BASE_DBMS_CAPABILITIES);
        return f2s;
    }

}
