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
 *     To address to an entity set, users can simply put the entity set name after the service
 *     root URI. The value of the property SHALL be a list of the entities in the specified
 *     entity set.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-collection-entities"> OGC SensorThings API Part 1: Sensing - Usage 2: address to a collection of entities</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetEntities extends RequestParameters {

    private String entitySet;
    private String parentEntitySet;
    private Object parentId;
    private String filter;
    private String expand;
    private String select;
    private String orderby;
    private Integer top;
    private Integer skip;
    private Boolean count;
    private String resultFormat;

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
     *     <li>The OData entity-set name to list, e.g. {@code "Things"}.</li>
     * </ul>
     * <pre>http://example.org/v1.1/ObservedProperties</pre>
     */
    public GetEntities entitySet(String entitySet) {
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
     *     As the entities in different entity sets may hold some relationships, users can
     *     request the linked entities by addressing to a navigation property of an entity.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#usage-address-navigation-property"> OGC SensorThings API Part 1: Sensing - Usage 6: address to a navigation property (navigationLink)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; set together with {@code parentId} to list a navigation property.</li>
     *     <li>E.g. {@code "Things"} for {@code Things(1)/Datastreams}.</li>
     * </ul>
     */
    public GetEntities parentEntitySet(String parentEntitySet) {
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
    public GetEntities parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * <blockquote><q>
     *     The $filter system query option allows clients to filter a collection of entities
     *     that are addressed by a request URL. The expression specified with $filter is
     *     evaluated for each entity in the collection, and only items where the expression
     *     evaluates to true SHALL be included in the response.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#filter"> OGC SensorThings API Part 1: Sensing - 9.3.3.5 $filter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional. Sent as-is to the server, not parsed client-side.</li>
     * </ul>
     * <pre>$filter=result lt 10.00</pre>
     */
    public GetEntities filter(String filter) {
        setFilter(filter);
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
     *     inline. The value of the $expand query option SHALL be a comma separated list of
     *     navigation property names.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#expand"> OGC SensorThings API Part 1: Sensing - 9.3.2.1 $expand</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional. Sent as-is to the server.</li>
     * </ul>
     * <pre>$expand=Datastreams</pre>
     */
    public GetEntities expand(String expand) {
        setExpand(expand);
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
     * </ul>
     * <pre>$select=result,resultTime</pre>
     */
    public GetEntities select(String select) {
        setSelect(select);
        return this;
    }

    /**
     * @see #orderby(java.lang.String)
     */
    public String getOrderby() {
        return orderby;
    }

    /**
     * @see #orderby(java.lang.String)
     */
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    /**
     * <blockquote><q>
     *     The $orderby system query option specifies the order in which items are returned
     *     from the service. The value of the $orderby system query option SHALL contain a
     *     comma-separated list of expressions whose primitive result values are used to sort
     *     the items.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#orderby"> OGC SensorThings API Part 1: Sensing - 9.3.3.1 $orderby</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional. Sent as-is to the server.</li>
     *     <li>Default sort order is ascending; each expression may be suffixed with
     *     {@code asc} or {@code desc}.</li>
     * </ul>
     * <pre>$orderby=result</pre>
     */
    public GetEntities orderby(String orderby) {
        setOrderby(orderby);
        return this;
    }

    /**
     * @see #top(java.lang.Integer)
     */
    public Integer getTop() {
        return top;
    }

    /**
     * @see #top(java.lang.Integer)
     */
    public void setTop(Integer top) {
        this.top = top;
    }

    /**
     * <blockquote><q>
     *     The $top system query option specifies the limit on the number of items returned
     *     from a collection of entities. The value of the $top system query option SHALL be a
     *     non-negative integer n.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#top"> OGC SensorThings API Part 1: Sensing - 9.3.3.2 $top</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Non-negative integer; the maximum number of entities to return.</li>
     * </ul>
     * <pre>$top=5</pre>
     */
    public GetEntities top(Integer top) {
        setTop(top);
        return this;
    }

    /**
     * @see #skip(java.lang.Integer)
     */
    public Integer getSkip() {
        return skip;
    }

    /**
     * @see #skip(java.lang.Integer)
     */
    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * <blockquote><q>
     *     The $skip system query option specifies the number for the items of the queried
     *     collection that SHALL be excluded from the result. The value of $skip system query
     *     option SHALL be a non-negative integer n.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#skip"> OGC SensorThings API Part 1: Sensing - 9.3.3.3 $skip</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Non-negative integer; the number of entities to skip.</li>
     * </ul>
     * <pre>$skip=5</pre>
     */
    public GetEntities skip(Integer skip) {
        setSkip(skip);
        return this;
    }

    /**
     * @see #count(java.lang.Boolean)
     */
    public Boolean getCount() {
        return count;
    }

    /**
     * @see #count(java.lang.Boolean)
     */
    public void setCount(Boolean count) {
        this.count = count;
    }

    /**
     * <blockquote><q>
     *     The $count system query option with a value of true specifies that the total count
     *     of items within a collection matching the request SHALL be returned along with the
     *     result.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#count"> OGC SensorThings API Part 1: Sensing - 9.3.3.4 $count</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>If {@code true}, the response includes the total number of matching entities as
     *     {@code @iot.count}.</li>
     * </ul>
     * <pre>$count=true</pre>
     */
    public GetEntities count(Boolean count) {
        setCount(count);
        return this;
    }

    /**
     * @see #resultFormat(java.lang.String)
     */
    public String getResultFormat() {
        return resultFormat;
    }

    /**
     * @see #resultFormat(java.lang.String)
     */
    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    /**
     * <blockquote><q>
     *     In order to request for dataArray, users must include the query option
     *     $resultFormat=dataArray when requesting Observation entities.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-088/18-088.html#_request_4"> OGC SensorThings API Part 1: Sensing - 13.1.1 Request (dataArray extension)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Passed through as-is; the compact dataArray response shape itself is not
     *     specially modeled by this client.</li>
     * </ul>
     * <pre>$resultFormat=dataArray</pre>
     */
    public GetEntities resultFormat(String resultFormat) {
        setResultFormat(resultFormat);
        return this;
    }

}
