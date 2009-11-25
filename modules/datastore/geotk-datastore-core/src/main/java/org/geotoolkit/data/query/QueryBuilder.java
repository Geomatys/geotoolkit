/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.query;

import org.geotoolkit.factory.Hints;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Query builder, convinient utility class to build queries.
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryBuilder {

    private static final String[] NO_PROPERTIES = new String[0];
    
    private Name typeName;

    private Filter filter = Filter.INCLUDE;
    private String[] properties = null;
    private SortBy[] sortBy = null;

    private CoordinateReferenceSystem crs = null;

    private Integer startIndex = null;
    private Integer maxFeatures = null;

    private Hints hints = null;

    private String handle = null;

    public QueryBuilder(){
    }

    public QueryBuilder copy(Query query){
        this.crs = query.getCoordinateSystemReproject();
        this.filter = query.getFilter();
        this.hints = query.getHints();
        this.maxFeatures = query.getMaxFeatures();
        this.properties = query.getPropertyNames();
        this.sortBy = query.getSortBy();
        this.startIndex = query.getStartIndex();
        this.typeName = query.getTypeName();
        this.handle = query.getHandle();
        return this;
    }

    public Name getTypeName() {
        return typeName;
    }

    public QueryBuilder setTypeName(Name typeName) {
        this.typeName = typeName;
        return this;
    }

    public Filter getFilter() {
        return filter;
    }

    public QueryBuilder setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public String[] getProperties() {
        return properties;
    }

    public QueryBuilder setProperties(String[] properties) {
        this.properties = properties;
        return this;
    }

    public SortBy[] getSortBy() {
        return sortBy;
    }

    public QueryBuilder setSortBy(SortBy[] sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public QueryBuilder setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public QueryBuilder setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
        return this;
    }

    public QueryBuilder setCRS(CoordinateReferenceSystem crs) {
        this.crs = crs;
        return this;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public Hints getHints() {
        return hints;
    }

    public QueryBuilder setHints(Hints hints) {
        this.hints = hints;
        return this;
    }

    public String getHandle() {
        return handle;
    }

    public QueryBuilder setHandle(String handle) {
        this.handle = handle;
        return this;
    }

    public Query buildQuery(){
        return new DefaultQuery(typeName, filter, properties, sortBy, crs, startIndex, maxFeatures, hints, handle);
    }

    /**
     * Create a simple query with only a filter parameter.
     * 
     * @param name
     * @param filter
     * @return Immutable query
     */
    public static Query filtered(Name name, Filter filter){
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(name);
        builder.setFilter(filter);
        return builder.buildQuery();
    }

    /**
     * Create a simple query with only a sorted parameter.
     *
     * @param name
     * @param filter
     * @return Immutable query
     */
    public static Query sorted(Name name, SortBy[] sorts){
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(name);
        builder.setSortBy(sorts);
        return builder.buildQuery();
    }

    /**
     * Implements a query that will fetch all features from a source. This
     * query should retrieve all properties, with no maxFeatures, no
     * filtering, and the default featureType.
     */
    public static Query all(Name name){
        return new DefaultQuery(name);
    }

    /**
     * Implements a query that will fetch all the FeatureIDs from a source.
     * This query should retrive no properties, with no maxFeatures, no
     * filtering, and the a featureType with no attribtues.
     */
    public static Query fids(Name name){
        return new DefaultQuery(name, NO_PROPERTIES);
    }

}
