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
 * Retrieve a single entity by id, e.g. {@code GET /v1.1/Things(1)}.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 9 (resource path).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetEntity extends RequestParameters {

    private String entitySet;
    private Object id;
    private String parentEntitySet;
    private Object parentId;
    private String select;
    private String expand;

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
    public GetEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * The {@code @iot.id} of the entity to retrieve. (required)
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
    public GetEntity id(Object id) {
        setId(id);
        return this;
    }

    /**
     * The entity set of the parent entity, when retrieving a navigation property. (optional)
     *
     * @return the parentEntitySet
     */
    public String getParentEntitySet() {
        return parentEntitySet;
    }

    /**
     * @param parentEntitySet the parentEntitySet to set
     * @see #getParentEntitySet()
     */
    public void setParentEntitySet(String parentEntitySet) {
        this.parentEntitySet = parentEntitySet;
    }

    /**
     * @param parentEntitySet the parentEntitySet to set
     * @see #getParentEntitySet()
     */
    public GetEntity parentEntitySet(String parentEntitySet) {
        setParentEntitySet(parentEntitySet);
        return this;
    }

    /**
     * The id of the parent entity, when retrieving a navigation property. (optional)
     *
     * @return the parentId
     */
    public Object getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     * @see #getParentId()
     */
    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    /**
     * @param parentId the parentId to set
     * @see #getParentId()
     */
    public GetEntity parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * The {@code $select} OData query option, a comma-separated list of property names.
     * (optional)
     *
     * @return the select
     */
    public String getSelect() {
        return select;
    }

    /**
     * @param select the select to set
     * @see #getSelect()
     */
    public void setSelect(String select) {
        this.select = select;
    }

    /**
     * @param select the select to set
     * @see #getSelect()
     */
    public GetEntity select(String select) {
        setSelect(select);
        return this;
    }

    /**
     * The {@code $expand} OData query option. Sent as-is to the server. (optional)
     *
     * @return the expand
     */
    public String getExpand() {
        return expand;
    }

    /**
     * @param expand the expand to set
     * @see #getExpand()
     */
    public void setExpand(String expand) {
        this.expand = expand;
    }

    /**
     * @param expand the expand to set
     * @see #getExpand()
     */
    public GetEntity expand(String expand) {
        setExpand(expand);
        return this;
    }

}
