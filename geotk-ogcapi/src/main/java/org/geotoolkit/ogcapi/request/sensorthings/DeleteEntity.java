/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.request.sensorthings;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     A successful DELETE request to an entity’s edit URL deletes the entity. The request body
 *     SHOULD be empty.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#_request_3"> OGC SensorThings API Part 1: Sensing - 10.4.1 Request</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteEntity extends RequestParameters {

    private String entitySet;
    private Object id;

    /**
     * @see #entitySet(java.lang.String)
     */
    public String getEntitySet() {
        return entitySet;
    }

    /**
     * @see #entitySet(java.lang.String)
     */
    public void setEntitySet(String entitySet) {
        this.entitySet = entitySet;
    }

    /**
     * <blockquote><q>
     *     To address to an entity set, users can simply put the entity set name after the
     *     service root URI.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-collection-entities"> OGC SensorThings API Part 1: Sensing - Usage 2: address to a collection of entities</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>E.g. {@code "Things"}.</li>
     * </ul>
     */
    public DeleteEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * @see #id(java.lang.Object)
     */
    public Object getId() {
        return id;
    }

    /**
     * @see #id(java.lang.Object)
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * <blockquote><q>
     *     Users can address to a specific entity in an entity set by place the unique identifier
     *     of the entity between brace symbol “()” and put after the entity set name.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-entity"> OGC SensorThings API Part 1: Sensing - Usage 3: address to an entity in a collection</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The {@code @iot.id} of the entity to delete.</li>
     * </ul>
     * <pre>http://example.org/v1.1/Things(1)</pre>
     */
    public DeleteEntity id(Object id) {
        setId(id);
        return this;
    }

}
