/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.internal.io;

import java.util.Properties;
import org.geotoolkit.util.logging.Logging;


/**
 * Holds the host and port number of a URL. This is equivalent to using the
 * {@link java.net.URI#getHost()} and {@link java.net.URI#getPort()} methods,
 * Except that no {@code URI} object is created and no exception it thrown.
 * <p>
 * This class is useful for URL that are not properly recognized by {@code URL}
 * or {@code URI}, like JDBC URL. It is used only for providing default values
 * in widgets.
 *
 * {@note This class is actually used by the <code>geotk-wizard-swing</code> module,
 *        but is nevertheless declared here because it could be used by more control
 *        panel widgets}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
public final class Host {
    /**
     * The host name, or {@code null} if none.
     */
    public String host;

    /**
     * The port number, or {@code null} if none.
     */
    public Integer port;

    /**
     * Creates a new {@code Host} for the given URL.
     *
     * @param url The URL, which may be {@code null}.
     */
    public Host(final String url) {
        if (url != null) {
            int lower = url.indexOf("://");
            if (lower >= 0) {
                lower += 3;
                final int upper = url.indexOf('/', lower);
                String server = (upper >= 0) ? url.substring(lower, upper) : url.substring(lower).trim();
                lower = server.indexOf(':');
                if (lower >= 0) {
                    server = server.substring(0, lower).trim();
                    try {
                        port = Integer.valueOf(server.substring(lower+1));
                    } catch (NumberFormatException e) {
                        Logging.recoverableException(Host.class, "<init>", e);
                        // Ignore, since this class is used only for providing
                        // default values in widgets.
                    }
                }
                if (server.length() != 0) {
                    host = server;
                }
            }
        }
    }

    /**
     * Creates a new {@code Host} for the {@code "URL"} value of the properties map.
     * The properties map is usually obtained by {@link Installation#getDataSource()}.
     *
     * @param properties The properties map, which may be {@code null}.
     * @param defaultValue The default value, or {@code null} if none.
     */
    public Host(final Properties properties, final String defaultValue) {
        this(properties != null ? properties.getProperty("URL", defaultValue) : defaultValue);
    }
}
