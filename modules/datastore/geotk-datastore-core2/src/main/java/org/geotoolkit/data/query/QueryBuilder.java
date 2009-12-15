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
    
    private Name typeName = null;

    private Filter filter = Filter.INCLUDE;
    private String[] properties = null;
    private SortBy[] sortBy = null;
    private CoordinateReferenceSystem crs = null;
    private Integer startIndex = null;
    private Integer maxFeatures = null;
    private Hints hints = null;

    public QueryBuilder(){
    }

    public QueryBuilder(Query query){
        copy(query);
    }

    public QueryBuilder(Name name){
        setTypeName(name);
    }

    public void reset(){
        typeName = null;
        filter = Filter.INCLUDE;
        properties = null;
        sortBy = null;
        crs = null;
        startIndex = null;
        maxFeatures = null;
        hints = null;
    }

    public void copy(Query query){
        this.crs = query.getCoordinateSystemReproject();
        this.filter = query.getFilter();
        this.hints = query.getHints();
        this.maxFeatures = query.getMaxFeatures();
        this.properties = query.getPropertyNames();
        this.sortBy = query.getSortBy();
        this.startIndex = query.getStartIndex();
        this.typeName = query.getTypeName();
    }

    public Name getTypeName() {
        return typeName;
    }

    public void setTypeName(Name typeName) {
        this.typeName = typeName;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public SortBy[] getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy[] sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public void setCRS(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public Hints getHints() {
        return hints;
    }

    public void setHints(Hints hints) {
        this.hints = hints;
    }

    public Query buildQuery(){
        return new DefaultQuery(typeName, filter, properties, sortBy, crs, startIndex, maxFeatures, hints);
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
