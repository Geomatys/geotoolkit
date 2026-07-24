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
 * Partially update an existing entity, e.g. {@code PATCH /v1.1/Things(1)}. Also the mechanism to
 * attach or detach a linked entity without touching its lifecycle: PATCH the owning entity's
 * navigation property with an id-only linked entity.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 10.4 (update entity).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class UpdateEntity extends RequestParameters {

    private String entitySet;
    private Object id;
    private byte[] content;
    private String contentType;

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
    public UpdateEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * The {@code @iot.id} of the entity to update. (required)
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
    public UpdateEntity id(Object id) {
        setId(id);
        return this;
    }

    /**
     * The raw partial entity document with the properties to update. (required)
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public UpdateEntity content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * The media type of the entity encoding, sent as the {@code Content-Type} header (e.g.
     * {@code application/json}). (required)
     *
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public UpdateEntity contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
