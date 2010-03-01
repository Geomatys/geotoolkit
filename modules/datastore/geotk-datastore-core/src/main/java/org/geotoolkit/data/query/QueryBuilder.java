/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Query builder, convinient utility class to build queries.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class QueryBuilder {

    private static final Name[] NO_PROPERTIES = new Name[0];

    private Source source = null;
    private Name typeName = null;

    private Filter filter = Filter.INCLUDE;
    private Name[] properties = null;
    private SortBy[] sortBy = null;
    private CoordinateReferenceSystem crs = null;
    private int startIndex = 0;
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
        startIndex = 0;
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
        this.source = query.getSource();
    }

    public Name getTypeName() {
        return typeName;
    }

    public void setTypeName(Name typeName) {
        this.typeName = typeName;
        this.source = null;
    }

    public void setSource(Source source){
        this.source = source;
        this.typeName = null;
    }

    public Source getSource(){
        return source;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Name[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        if(properties == null){
            this.properties = null;
        }else{
            this.properties = new Name[properties.length];
            for(int i=0;i<properties.length;i++){
                this.properties[i] = DefaultName.valueOf(properties[i]);
            }
        }
    }

    public void setProperties(Name[] properties) {
        this.properties = properties;
    }

    public SortBy[] getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy[] sortBy) {
        this.sortBy = sortBy;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
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
        final Source cs = (source == null) ? new DefaultSelector(null, typeName, "s1") : source;
        checkSource(cs,null);
        return new DefaultQuery(cs, filter, properties, sortBy, crs, startIndex, maxFeatures, hints);
    }

    /**
     * Verify that we don't have several selectors with the same name.
     */
    private static void checkSource(Source s, Set<String> selectors){
        if(selectors == null){
            selectors = new HashSet<String>();
        }

        if(s instanceof Join){
            final Join j = (Join) s;
            checkSource(j.getLeft(), selectors);
            checkSource(j.getRight(), selectors);
        }else if(s instanceof Selector){
            final String selectName = ((Selector) s).getSelectorName();
            if(selectors.contains(selectName)){
                throw new IllegalStateException("Source has several selector with the same name = " + selectName);
            }else{
                selectors.add(selectName);
            }
        }else{
            throw new IllegalStateException("Source type is unknowned : " + s +
                    "\n valid types ares Join and Selector");
        }

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
        return new DefaultQuery(new DefaultSelector(null, name, "s1"));
    }

    /**
     * Implements a query that will fetch all features from a source. This
     * query should retrieve all properties, with no maxFeatures, no
     * filtering, and the default featureType.
     */
    public static Query all(Source source){
        return new DefaultQuery(source);
    }

    /**
     * Implements a query that will fetch all the FeatureIDs from a source.
     * This query should retrive no properties, with no maxFeatures, no
     * filtering, and the a featureType with no attribtues.
     */
    public static Query fids(Name name){
        return new DefaultQuery(new DefaultSelector(null, name, "s1"), NO_PROPERTIES);
    }

    /**
     * Create a simple query with only a reproject crs.
     *
     * @param name
     * @param filter
     * @return Immutable query
     */
    public static Query reprojected(Name name, CoordinateReferenceSystem crs){
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(name);
        builder.setCRS(crs);
        return builder.buildQuery();
    }

}
