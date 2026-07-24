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
 * List the entities of an entity set, e.g. {@code GET /v1.1/Things} or, nested under a parent
 * entity, {@code GET /v1.1/Things(1)/Datastreams}.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 9 (resource path) and section 10.2 (query
 * options).</p>
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
     * The OData entity-set name to list, e.g. {@code "Things"}. (required)
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
    public GetEntities entitySet(String entitySet) {
        setEntitySet(entitySet);
        return this;
    }

    /**
     * The entity set of the parent entity, when listing a navigation property, e.g.
     * {@code "Things"} for {@code Things(1)/Datastreams}. (optional)
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
    public GetEntities parentEntitySet(String parentEntitySet) {
        setParentEntitySet(parentEntitySet);
        return this;
    }

    /**
     * The id of the parent entity, when listing a navigation property. (optional)
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
    public GetEntities parentId(Object parentId) {
        setParentId(parentId);
        return this;
    }

    /**
     * The {@code $filter} OData query option. Sent as-is to the server, not parsed client-side.
     * (optional)
     *
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public GetEntities filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * The {@code $expand} OData query option, e.g. {@code "Datastreams($select=name)"}. Sent
     * as-is to the server. (optional)
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
    public GetEntities expand(String expand) {
        setExpand(expand);
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
    public GetEntities select(String select) {
        setSelect(select);
        return this;
    }

    /**
     * The {@code $orderby} OData query option, e.g. {@code "phenomenonTime desc"}. Sent as-is to
     * the server. (optional)
     *
     * @return the orderby
     */
    public String getOrderby() {
        return orderby;
    }

    /**
     * @param orderby the orderby to set
     * @see #getOrderby()
     */
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    /**
     * @param orderby the orderby to set
     * @see #getOrderby()
     */
    public GetEntities orderby(String orderby) {
        setOrderby(orderby);
        return this;
    }

    /**
     * The {@code $top} OData query option: the maximum number of entities to return. (optional)
     *
     * @return the top
     */
    public Integer getTop() {
        return top;
    }

    /**
     * @param top the top to set
     * @see #getTop()
     */
    public void setTop(Integer top) {
        this.top = top;
    }

    /**
     * @param top the top to set
     * @see #getTop()
     */
    public GetEntities top(Integer top) {
        setTop(top);
        return this;
    }

    /**
     * The {@code $skip} OData query option: the number of entities to skip. (optional)
     *
     * @return the skip
     */
    public Integer getSkip() {
        return skip;
    }

    /**
     * @param skip the skip to set
     * @see #getSkip()
     */
    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * @param skip the skip to set
     * @see #getSkip()
     */
    public GetEntities skip(Integer skip) {
        setSkip(skip);
        return this;
    }

    /**
     * The {@code $count} OData query option: if {@code true}, the response includes the total
     * number of matching entities as {@code @iot.count}. (optional)
     *
     * @return the count
     */
    public Boolean getCount() {
        return count;
    }

    /**
     * @param count the count to set
     * @see #getCount()
     */
    public void setCount(Boolean count) {
        this.count = count;
    }

    /**
     * @param count the count to set
     * @see #getCount()
     */
    public GetEntities count(Boolean count) {
        setCount(count);
        return this;
    }

    /**
     * The {@code $resultFormat} OData query option, e.g. {@code "dataArray"} for the compact
     * Observation array encoding. Passed through as-is; the compact response shape itself is not
     * specially modeled by this client. (optional)
     *
     * @return the resultFormat
     */
    public String getResultFormat() {
        return resultFormat;
    }

    /**
     * @param resultFormat the resultFormat to set
     * @see #getResultFormat()
     */
    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    /**
     * @param resultFormat the resultFormat to set
     * @see #getResultFormat()
     */
    public GetEntities resultFormat(String resultFormat) {
        setResultFormat(resultFormat);
        return this;
    }

}
