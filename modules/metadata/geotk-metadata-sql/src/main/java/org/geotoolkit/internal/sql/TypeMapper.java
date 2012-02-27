/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.sql;

import java.util.Date;
import java.util.Arrays;
import java.sql.Types;

import org.geotoolkit.lang.Static;


/**
 * Maps a few basic Java types to JDBC types.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.03
 * @module
 */
public final class TypeMapper extends Static {
    /**
     * A list of Java classes to be mapped to SQL types. We don't want to map every SQL types,
     * but only the ones which is of interest for the Geotk metadata implementation. The types
     * will be tested in the order they are declared, so the last declarations are fallbacks.
     * <p>
     * The types declared here matches both the JavaDB and PostgreSQL mapping.
     */
    private static final TypeMapper[] TYPES = {
        // Note: JavaDB does not supports BOOLEAN as of Derby 10.5.
        // See http://issues.apache.org/jira/browse/DERBY-499
        new TypeMapper(Boolean.class, Types.BOOLEAN,   "BOOLEAN"),
        new TypeMapper(Date   .class, Types.TIMESTAMP, "TIMESTAMP"),
        new TypeMapper(Double .class, Types.DOUBLE,    "DOUBLE PRECISION"),
        new TypeMapper(Float  .class, Types.REAL  ,    "REAL"),
        new TypeMapper(Long   .class, Types.BIGINT,    "BIGINT"),
        new TypeMapper(Integer.class, Types.INTEGER,   "INTEGER"),
        new TypeMapper(Short  .class, Types.SMALLINT,  "SMALLINT"),
        new TypeMapper(Byte   .class, Types.TINYINT,   "SMALLINT"), // JavaDB does not support TINYINT.
        new TypeMapper(Number .class, Types.DECIMAL,   "DECIMAL")   // Implemented by BigDecimal.
    };

    /**
     * The value to add to {@link #type} in order to get only positive or null values.
     */
    private static final int OFFSET = 6;

    /**
     * Maps SQL types to index in the {@link #TYPES} array.
     */
    private static final byte[] ITYPES;
    static {
        ITYPES = new byte[100];
        Arrays.fill(ITYPES, (byte) -1);
        for (byte i=0; i<TYPES.length; i++) {
            ITYPES[TYPES[i].type + OFFSET] = i;
        }
    }

    /**
     * The Java class.
     */
    private final Class<?> classe;

    /**
     * A constant from the SQL {@link Types} enumeration.
     */
    private final int type;

    /**
     * The SQL keyword for that type.
     */
    private final String keyword;

    /**
     * For internal use only.
     */
    private TypeMapper(final Class<?> classe, final int type, final String keyword) {
        this.classe  = classe;
        this.type    = type;
        this.keyword = keyword;
    }

    /**
     * Returns the SQL keyword for storing an element of the given type, or {@code null} if
     * unknown. This method does not handle the text type, so {@link String} are treated as
     * "unknown" as well. We do that way because the caller will need to specify a value in
     * its {@code VARCHAR(n)} statement.
     *
     * @param  classe The class for which to get the SQL keyword in a {@code CREATE TABLE} statement.
     * @return The SQL keyword, or {@code null} if unknown.
     */
    public static String keywordFor(final Class<?> classe) {
        if (classe != null) {
            for (final TypeMapper type : TYPES) {
                if (type.classe.isAssignableFrom(classe)) {
                    return type.keyword;
                }
            }
        }
        return null;
    }

    /**
     * Return the Java class for the given SQL type, or {@code null} if none.
     *
     * @param  sqlType One of the {@link Types} constants.
     * @return The Java class, or {@code null} if none.
     *
     * @since 3.09
     */
    public static Class<?> toJavaType(int sqlType) {
        sqlType += OFFSET;
        if (sqlType >= 0 && sqlType < ITYPES.length) {
            final byte mapper = ITYPES[sqlType];
            if (mapper >= 0 && mapper < TYPES.length) {
                return TYPES[mapper].classe;
            }
        }
        return null;
    }
}
