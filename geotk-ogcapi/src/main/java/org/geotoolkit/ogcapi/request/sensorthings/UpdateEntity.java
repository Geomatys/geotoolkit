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
 *     In SensorThings PATCH is the preferred means of updating an entity. PATCH provides more
 *     resiliency between clients and services by directly modifying only those values
 *     specified by the client.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#_request_2"> OGC SensorThings API Part 1: Sensing - 10.3.1 Request</a></small></i>
 * </blockquote>
 *
 * <p>Also the mechanism to attach or detach a linked entity without touching its lifecycle:
 * PATCH the owning entity's navigation property with an id-only linked entity.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class UpdateEntity extends RequestParameters {

    private String entitySet;
    private Object id;
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
    public UpdateEntity entitySet(String entitySet) {
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
     *     <li>The {@code @iot.id} of the entity to update.</li>
     * </ul>
     * <pre>http://example.org/v1.1/Things(1)</pre>
     */
    public UpdateEntity id(Object id) {
        setId(id);
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
     *     The semantics of PATCH, as defined in [RFC5789], are to merge the content in the
     *     request payload with the entity's current state, applying the update only to those
     *     components specified in the request body.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#_request_2"> OGC SensorThings API Part 1: Sensing - 10.3.1 Request</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw partial entity document with the properties to update, encoded as
     *     declared by {@code contentType}.</li>
     * </ul>
     */
    public UpdateEntity content(byte[] content) {
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
    public UpdateEntity contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
