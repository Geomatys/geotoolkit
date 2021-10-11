/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.manager;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.sis.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class LuceneSQLTreeEltMapper {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.index.tree.manager");

     /**
     * Mutual Coordinate Reference System from all stored NamedEnvelopes.
     */
    protected final CoordinateReferenceSystem crs;

    protected final DataSource source;

    public LuceneSQLTreeEltMapper(CoordinateReferenceSystem crs, DataSource source) {
        this.crs = crs;
        this.source = source;
    }

    /**
     * Return an input stream of the specified resource.
     */
    protected static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }

    /**
     * Obtain the Thread Context ClassLoader.
     */
    protected static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

}
