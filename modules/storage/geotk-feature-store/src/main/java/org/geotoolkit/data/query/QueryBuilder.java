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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.internal.feature.AttributeConvention;


/**
 * Query builder, convenient utility class to build queries.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class QueryBuilder {

    private static final GenericName[] ONLY_ID_PROPERTIES = new GenericName[]{
        AttributeConvention.IDENTIFIER_PROPERTY
    };

    private Source source = null;
    private String typeName = null;

    private Filter filter = Filter.INCLUDE;
    private GenericName[] properties = null;
    private SortBy[] sortBy = null;
    private CoordinateReferenceSystem crs = null;
    private int startIndex = 0;
    private Integer maxFeatures = null;
    private Hints hints = null;
    private double[] resolution = null;
    private String language = Query.GEOTK_QOM;
    private Object version = null;

    public QueryBuilder(){
    }

    public QueryBuilder(final Query query){
        copy(query);
    }

    public QueryBuilder(final String name){
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
        resolution = null;
        hints = null;
        version = null;
    }

    public void copy(final Query query){
        this.crs = query.getCoordinateSystemReproject();
        this.resolution = (query.getResolution()==null)?null:query.getResolution().clone();
        this.filter = query.getFilter();
        this.hints = query.getHints();
        this.maxFeatures = query.getMaxFeatures();
        this.properties = query.getPropertyNames();
        this.sortBy = query.getSortBy();
        this.startIndex = query.getStartIndex();
        this.typeName = query.getTypeName();
        this.source = query.getSource();
        this.version = query.getVersionDate();
        if(this.version==null) this.version = query.getVersionLabel();
        this.language = query.getLanguage();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final GenericName typeName) {
        this.typeName = typeName.toString();
        this.source = null;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
        this.source = null;
    }

    public void setSource(final Source source){
        this.source = source;
        this.typeName = null;
    }

    public Source getSource(){
        if(source == null){
            return new DefaultSelector(null, typeName, "s1");
        }else{
            return source;
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    public GenericName[] getProperties() {
        return properties;
    }

    public void setProperties(final String[] properties) {
        if(properties == null){
            this.properties = null;
        }else{
            this.properties = new GenericName[properties.length];
            for(int i=0;i<properties.length;i++){
                this.properties[i] = NamesExt.valueOf(properties[i]);
            }
        }
    }

    public void setProperties(final GenericName[] properties) {
        this.properties = properties;
    }

    public SortBy[] getSortBy() {
        return sortBy;
    }

    public void setSortBy(final SortBy[] sortBy) {
        this.sortBy = sortBy;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(final int startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(final Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public void setCRS(final CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public void setResolution(final double[] resolution) {
        this.resolution = resolution;
    }

    public double[] getResolution() {
        return resolution;
    }

    public void setVersionLabel(String label) {
        this.version = label;
    }

    public String getVersionLabel() {
        if(version instanceof String){
            return (String)version;
        }
        return null;
    }

    public void setVersionDate(Date version) {
        this.version = version;
    }

    public Date getVersionDate() {
        if(version instanceof Date){
            return (Date)version;
        }
        return null;
    }

    public Hints getHints() {
        return hints;
    }

    public void setHints(final Hints hints) {
        this.hints = hints;
    }

    public Query buildQuery(){
        final Source cs = (source == null) ? new DefaultSelector(null, typeName, "s1") : source;
        checkSource(cs,null);
        if(cs instanceof TextStatement){
            return new DefaultQuery(language, (TextStatement)cs);
        }else{
            return new DefaultQuery(cs, filter, properties, sortBy, crs, startIndex, maxFeatures, resolution, version, hints);
        }
    }

    /**
     * Verify that we don't have several selectors with the same name.
     */
    private static void checkSource(final Source s, Set<String> selectors){
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
        }else if(s instanceof TextStatement){
            //can't check this
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
    public static Query filtered(final String name, final Filter filter){
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
    public static Query sorted(final String name, final SortBy ... sorts){
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
    public static Query all(final GenericName name){
        return new DefaultQuery(new DefaultSelector(null, name.toString(), "s1"));
    }

    /**
     * Implements a query that will fetch all features from a source. This
     * query should retrieve all properties, with no maxFeatures, no
     * filtering, and the default featureType.
     */
    public static Query all(final String name){
        return new DefaultQuery(new DefaultSelector(null, name, "s1"));
    }

    /**
     * Implements a query that will fetch all features from a source. This
     * query should retrieve all properties, with no maxFeatures, no
     * filtering, and the default featureType.
     */
    public static Query all(final Source source){
        return new DefaultQuery(source);
    }

    /**
     * Create a query in the defined language.
     *
     * @param language
     * @param statement
     * @param name
     * @return Query
     */
    public static Query language(final String language, final String statement, final String name){
        return new DefaultQuery(language, statement,name);
    }

    /**
     * Implements a query that will fetch all the FeatureIDs from a source.
     * This query should retrive no properties, with no maxFeatures, no
     * filtering, and the a featureType with no attributes.
     */
    public static Query fids(final String name){
        return new DefaultQuery(new DefaultSelector(null, name, "s1"), ONLY_ID_PROPERTIES);
    }

    /**
     * Create a simple query with only a reproject crs.
     *
     * @param name
     * @param filter
     * @return Immutable query
     */
    public static Query reprojected(final String name, final CoordinateReferenceSystem crs){
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(name);
        builder.setCRS(crs);
        return builder.buildQuery();
    }

    /**
     *
     * @param sortBy array or null
     * @return true is the given array of sort by operand is equal to natural sort by
     */
    public static boolean isNaturalSortBy(SortBy[] sortBy){
        if(sortBy == null || sortBy.length == 0){
            return true;
        }

        for(SortBy sb : sortBy){
            if(sb != SortBy.NATURAL_ORDER){
                return false;
            }
        }

        return true;
    }

}
