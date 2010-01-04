/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.rtree.cachefs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class NodeCache extends LinkedHashMap {
    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging
            .getLogger("org.geotoolkit.index.rtree");
    private final int maxElements;

    /**
     * Constructor
     */
    public NodeCache() {
        this(100);
    }

    /**
     * Constructor
     * 
     * @param capacity
     *                the capacity of the cache
     */
    public NodeCache(int capacity) {
        super(capacity);
        this.maxElements = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        boolean ret = this.size() > this.maxElements;

        if (ret) {
            try {
                ((FileSystemNode) eldest.getValue()).flush();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return ret;
    }
}
