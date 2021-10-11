/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2016, Geomatys
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
package org.geotoolkit.index;

/**
 * A Spatial query use to perform search request on various datasource.
 * It combines a textual query in the language of the implementation (Lucene, ElasticSearch, SQL, ...)
 * and some implementation specific query/filter object.
 *
 * @author Guilhem Legal(Geomatys)
 */
public interface SpatialQuery {

    /**
     * Return a textual query.
     * can be {@code null}.
     */
    String getTextQuery();

    /**
     * Return a implementation specific query/filter object.
     * can be {@code null}.
     */
    Object getQuery();

    /**
     * Return a implementation specific sort object.
     * can be {@code null}.
     */
    Object getSort();

    /**
     * Add a single sort order to the query on a field.
     *
     * @param fieldName the property name on which the sort apply.
     * @param desc {@code true} for DESCENDING order, {@code false} for ASCENDING.
     * @param fieldType The data type off the field (implementation specific) describing the field type (like Integer, Double, String, ...).
     */
    void setSort(final String fieldName, final boolean desc, Character fieldType);
}
