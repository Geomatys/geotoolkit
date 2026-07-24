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
 * Create a new entity, e.g. {@code POST /v1.1/Things}, or {@code POST /v1.1/TaskingCapabilities(1)/Tasks}
 * for the one documented nested-create case (creating a Task under its TaskingCapability).
 *
 * <p>The request body may use deep insert: nested entities with every field populated are created
 * along with the top-level entity; nested entities with only {@code @iot.id} populated are linked
 * to an existing entity instead.</p>
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 10.3 (create entity); OGC SensorThings API
 * Part 2: Tasking Core, section 4 (Task creation).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class CreateEntity extends RequestParameters {

    private String entitySet;
    private String parentEntitySet;
    private Object parentId;
    private byte[] content;
    private String contentType;

    /**
     * The OData entity-set name to create the entity in, e.g. {@code "Things"}. (required)
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
    public CreateEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * The entity set of the parent entity, for the nested-create case (e.g. {@code "TaskingCapabilities"}
     * when creating a {@code Task}). (optional)
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
    public CreateEntity parentEntitySet(String parentEntitySet) {
        setParentEntitySet(parentEntitySet);
        return this;
    }

    /**
     * The id of the parent entity, for the nested-create case. (optional)
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
    public CreateEntity parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * The raw entity document to publish. (required)
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
    public CreateEntity content(byte[] content) {
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
    public CreateEntity contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
