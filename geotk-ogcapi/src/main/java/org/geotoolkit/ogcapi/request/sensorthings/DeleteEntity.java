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
 * Delete an existing entity, e.g. {@code DELETE /v1.1/Things(1)}.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 10.5 (delete entity).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteEntity extends RequestParameters {

    private String entitySet;
    private Object id;

    /**
     * The OData entity-set name, e.g. {@code "Things"}. (required)
     *
     * @return the entitySet
     */
    public String getEntitySet() {
        return entitySet;
    }

    /**
     * @param entitySet the entitySet to set
     * @see #getEntitySet()
     */
    public void setEntitySet(String entitySet) {
        this.entitySet = entitySet;
    }

    /**
     * @param entitySet the entitySet to set
     * @see #getEntitySet()
     */
    public DeleteEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * The {@code @iot.id} of the entity to delete. (required)
     *
     * @return the id
     */
    public Object getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public DeleteEntity id(Object id) {
        setId(id);
        return this;
    }

}
