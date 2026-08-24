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
 *     To create an entity in a collection, the client SHALL send a HTTP POST request to that
 *     collection’s URL. The POST body SHALL contain a single valid entity representation.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#create-entity"> OGC SensorThings API Part 1: Sensing - 10.2 Create an entity</a></small></i>
 * </blockquote>
 *
 * <p>The request body may use deep insert: nested entities with every field populated are created
 * along with the top-level entity; nested entities with only {@code @iot.id} populated are linked
 * to an existing entity instead.</p>
 *
 * <p>This request also covers the one nested-create case documented by OGC SensorThings API
 * Part 2: Tasking Core, section 4: creating a Task under its TaskingCapability
 * ({@code POST /v1.1/TaskingCapabilities(1)/Tasks}).</p>
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
     *     To create an entity in a collection, the client SHALL send a HTTP POST request to
     *     that collection’s URL.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#create-entity"> OGC SensorThings API Part 1: Sensing - 10.2 Create an entity</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The OData entity-set name to create the entity in, e.g. {@code "Things"}.</li>
     * </ul>
     */
    public CreateEntity entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * @see #parentEntitySet(java.lang.String)
     */
    public String getParentEntitySet() {
        return parentEntitySet;
    }

    /**
     * @see #parentEntitySet(java.lang.String)
     */
    public void setParentEntitySet(String parentEntitySet) {
        this.parentEntitySet = parentEntitySet;
    }

    /**
     * <blockquote><q>
     *     If the target URL for the collection is a navigationLink, the new entity is
     *     automatically linked to the entity containing the navigationLink.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#create-entity"> OGC SensorThings API Part 1: Sensing - 10.2 Create an entity</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; set together with {@code parentId} for the nested-create case.</li>
     *     <li>E.g. {@code "TaskingCapabilities"} when creating a {@code Task}.</li>
     * </ul>
     */
    public CreateEntity parentEntitySet(String parentEntitySet) {
        setParentEntitySet(parentEntitySet);
        return this;
    }

    /**
     * @see #parentId(java.lang.Object)
     */
    public Object getParentId() {
        return parentId;
    }

    /**
     * @see #parentId(java.lang.Object)
     */
    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    /**
     * <blockquote><q>
     *     If the target URL for the collection is a navigationLink, the new entity is
     *     automatically linked to the entity containing the navigationLink.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#create-entity"> OGC SensorThings API Part 1: Sensing - 10.2 Create an entity</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the {@code @iot.id} of the parent entity, for the nested-create case.</li>
     * </ul>
     */
    public CreateEntity parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * <blockquote><q>
     *     The POST body SHALL contain a single valid entity representation.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#create-entity"> OGC SensorThings API Part 1: Sensing - 10.2 Create an entity</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw entity document, encoded as declared by {@code contentType}.</li>
     * </ul>
     */
    public CreateEntity content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * The media type of the entity encoding, sent as the {@code Content-Type} header (e.g.
     * {@code application/json}).
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>OGC SensorThings API Part 1: Sensing represents entities as JSON throughout (no
     *     verbatim spec text mandates a specific {@code Content-Type} value for this field).</li>
     * </ul>
     */
    public CreateEntity contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
