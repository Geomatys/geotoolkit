/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;


/**
 * A key for a configurable aspect of a {@linkplain Database database}.
 * They are typically keys in a {@linkplain java.util.Properties properties map}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Eve (IRD)
 * @version 3.11
 *
 * @since 3.09 (derived from Seagis)
 * @module
 *
 * @see Database#getProperty
 */
public enum ConfigurationKey {
    /**
     * The URL to the database. This is used only if no {@link javax.sql.DataSource}
     * were explicitly specified. For simplicity, it should be the same key than the
     * one used for the URL of the EPSG databases.
     *
     * @since 3.11
     */
    URL("URL", null),

    /**
     * Key for the local data directory root. The value may be {@code null} if data are
     * not accessible locally. In such case, data may be accessible remotely from the
     * {@link #ROOT_URL}.
     * <p>
     * The default value is {@code null}.
     */
    ROOT_DIRECTORY("rootDirectory", null),

    /**
     * Key for the URL to the server that host the data.
     * The default value is {@code "ftp://localhost/"}.
     */
    ROOT_URL("rootURL", "ftp://localhost/"),

    /**
     * Key for the timezone. This apply to the dates that appear in the database.
     * The {@code "local"} value is a special string for the local timezone.
     * <p>
     * The default value is {@code "UTC"}.
     */
    TIMEZONE("timezone", "UTC"),

    /**
     * The database catalog to use, or {@code null} if none. This is not widely used except
     * by Oracle.
     */
    CATALOG("catalog", null),

    /**
     * The database schema to use, or {@code null} if none. If {@code null}, then the tables
     * will be located using the default mechanism on the underlying database. On PostgreSQL,
     * the search order is determined by the {@code "search_path"} database variable.
     */
    SCHEMA("schema", null),

    /**
     * Key for user name connecting to the {@linkplain #DATABASE database}.
     * If {@code null}, then the connection will be fetched with the
     * {@link javax.sql.DataSource#getConnection()} method.
     */
    USER("user", null),

    /**
     * Key for {@linkplain #USER user} password.
     * If {@code null}, then the connection will be fetched with the
     * {@link javax.sql.DataSource#getConnection()} method.
     */
    PASSWORD("password", null);

    /**
     * A key to use for specifying a single {@link org.opengis.parameter.ParameterValueGroup}
     * value instead than the above-cited property keys.
     *
     * @since 3.18
     */
    public static final String PARAMETERS = "parameters";

    /**
     * The key used in a property file for storing a value for this enum.
     */
    public final String key;

    /**
     * The default value for this enum, which may be {@code null}. This default value is
     * used only if the user didn't specified explicitly a value in his property file.
     */
    public final String defaultValue;

    /**
     * Creates a new enum.
     */
    private ConfigurationKey(final String key, final String defaultValue) {
        this.key          = key.trim();
        this.defaultValue = defaultValue;
    }
}
