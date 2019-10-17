/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2013, Geomatys
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

package org.geotoolkit.data.query;

/**
 * Gives some information about the capabilities of the feature store.
 * Some feature store might not be able to handle sort by parameters for example.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface QueryCapabilities {

    /**
     * Returns an array of strings representing all query languages supported by
     * this feature store. This set must include at least the strings represented
     * by the constants {@link Query#GEOTK_QOM}. An implementation may also
     * support other languages.
     *
     * @return A string array.
     */
    public String[] getSupportedQueryLanguages();

    /**
     * Check if this feature store support join queries on it's own feature types.
     * @return true if the feature store can handle queries with join operations.
     */
    boolean handleCrossQuery();

    /**
     * Check if this feature store support versioning.
     * @return true if versioning is supported.
     */
    boolean handleVersioning();

}
