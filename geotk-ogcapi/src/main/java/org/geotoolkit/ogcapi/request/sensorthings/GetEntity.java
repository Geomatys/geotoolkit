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
 *     Users can address to a specific entity in an entity set by place the unique identifier of
 *     the entity between brace symbol “()” and put after the entity set name.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-entity"> OGC SensorThings API Part 1: Sensing - Usage 3: address to an entity in a collection</a></small></i>
 * </blockquote>
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
    public GetEntity entitySet(String entitySet) {
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
     *     <li>The {@code @iot.id} of the entity to retrieve.</li>
     * </ul>
     * <pre>http://example.org/v1.1/Things(1)</pre>
     */
    public GetEntity id(Object id) {
        setId(id);
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
     *     As the entities in different entity sets may hold some relationships, users can
     *     request the linked entities by addressing to a navigation property of an entity.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-navigation-property"> OGC SensorThings API Part 1: Sensing - Usage 6: address to a navigation property (navigationLink)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; set together with {@code parentId} to retrieve a navigation property.</li>
     *     <li>E.g. {@code "Things"} for {@code Things(1)/Datastreams}.</li>
     * </ul>
     */
    public GetEntity parentEntitySet(String parentEntitySet) {
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
     *     As the entities in different entity sets may hold some relationships, users can
     *     request the linked entities by addressing to a navigation property of an entity.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-navigation-property"> OGC SensorThings API Part 1: Sensing - Usage 6: address to a navigation property (navigationLink)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the {@code @iot.id} of the parent entity.</li>
     * </ul>
     * <pre>http://example.org/v1.1/Datastreams(1)/Observations</pre>
     */
    public GetEntity parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * @see #select(java.lang.String)
     */
    public String getSelect() {
        return select;
    }

    /**
     * @see #select(java.lang.String)
     */
    public void setSelect(String select) {
        this.select = select;
    }

    /**
     * <blockquote><q>
     *     The $select system query option requests the service to return only the properties
     *     explicitly requested by the client. The value of a $select query option SHALL be a
     *     comma-separated list of selection clauses.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#select4"> OGC SensorThings API Part 1: Sensing - 9.3.2.2 $select</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Comma-separated list of property names (including navigation property names).</li>
     * </ul>
     * <pre>$select=result,resultTime</pre>
     */
    public GetEntity select(String select) {
        setSelect(select);
        return this;
    }

    /**
     * @see #expand(java.lang.String)
     */
    public String getExpand() {
        return expand;
    }

    /**
     * @see #expand(java.lang.String)
     */
    public void setExpand(String expand) {
        this.expand = expand;
    }

    /**
     * <blockquote><q>
     *     The $expand system query option indicates the related entities to be represented
     *     inline.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#expand"> OGC SensorThings API Part 1: Sensing - 9.3.2.1 $expand</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional. Sent as-is to the server.</li>
     * </ul>
     * <pre>$expand=Datastreams</pre>
     */
    public GetEntity expand(String expand) {
        setExpand(expand);
        return this;
    }

}
