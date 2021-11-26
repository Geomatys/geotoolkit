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

package org.geotoolkit.storage.feature.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.query.FeatureQuery;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.SortProperty;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;


/**
 * Query builder, convenient utility class to build queries.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 * @deprecated use SIS FeatureQuery
 */
@Deprecated
public final class QueryBuilder {

    private static final String[] ONLY_ID_PROPERTIES = new String[]{
        AttributeConvention.IDENTIFIER
    };

    private String typeName = null;

    private Filter filter = Filter.include();
    private String[] properties = null;
    private SortProperty[] sortBy = null;
    private long startIndex = 0;
    private long maxFeatures = -1;
    private Hints hints = null;
    private double[] resolution = null;
    private Object version = null;
    private Quantity<Length> linearResolution;

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
        filter = Filter.include();
        properties = null;
        sortBy = null;
        startIndex = 0;
        maxFeatures = -1;
        resolution = null;
        hints = null;
        version = null;
    }

    public void copy(final Query query){
        this.resolution = (query.getResolution()==null)?null:query.getResolution().clone();
        this.filter = query.getSelection();
        this.hints = query.getHints();
        this.maxFeatures = query.getLimit();
        this.properties = query.getPropertyNames();
        this.sortBy = query.getSortBy();
        this.startIndex = query.getOffset();
        this.typeName = query.getTypeName();
        this.version = query.getVersionDate();
        if(this.version==null) this.version = query.getVersionLabel();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final GenericName typeName) {
        this.typeName = typeName.toString();
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(final String[] properties) {
        this.properties = properties;
    }

    public SortProperty[] getSortBy() {
        return sortBy;
    }

    public void setSortBy(final SortProperty[] sortBy) {
        this.sortBy = sortBy;
    }

    public long getOffset() {
        return startIndex;
    }

    public void setOffset(final long startIndex) {
        this.startIndex = startIndex;
    }

    public long getLimit() {
        return maxFeatures;
    }

    public void setLimit(final long maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    @Deprecated
    public void setResolution(final double[] resolution) {
        this.resolution = resolution;
    }

    @Deprecated
    public double[] getResolution() {
        return resolution;
    }

    public Quantity<Length> getLinearResolution() {
        return linearResolution;
    }

    public void setLinearResolution(Quantity<Length> linearResolution) {
        this.linearResolution = linearResolution;
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
        return new Query(typeName, filter, properties, sortBy, startIndex, maxFeatures, resolution, linearResolution, version, hints);
    }

    /**
     * Create a simple query with only a filter parameter.
     *
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
     * @return Immutable query
     */
    public static Query sorted(final String name, final SortProperty ... sorts){
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
        return new Query(name.toString());
    }

    /**
     * Implements a query that will fetch all features from a source. This
     * query should retrieve all properties, with no maxFeatures, no
     * filtering, and the default featureType.
     */
    public static Query all(final String name){
        return new Query(name);
    }

    /**
     * Implements a query that will fetch all the FeatureIDs from a source.
     * This query should retrive no properties, with no maxFeatures, no
     * filtering, and the a featureType with no attributes.
     */
    public static Query fids(final String name){
        return new Query(name, ONLY_ID_PROPERTIES);
    }

    /**
     * Create a simple query with columns which transform all geometric types to the given crs.
     */
    public static FeatureQuery reproject(FeatureType type, final CoordinateReferenceSystem crs) {
        final FilterFactory ff = FilterUtilities.FF;
        final Literal crsLiteral = ff.literal(crs);

        final FeatureQuery query = new FeatureQuery();
        final List<FeatureQuery.NamedExpression> columns = new ArrayList<>();

        for (PropertyType pt : type.getProperties(true)) {
            final GenericName name = pt.getName();
            Expression property = ff.property(name.toString());

            //unroll operation
            IdentifiedType result = pt;
            while (result instanceof Operation) {
                result = ((Operation) result).getResult();
            }
            if (result instanceof AttributeType) {
                AttributeType at = (AttributeType) result;
                if (Geometry.class.isAssignableFrom(at.getValueClass())) {
                    property = ff.function("ST_Transform", property, crsLiteral);
                }
            }
            columns.add(new FeatureQuery.NamedExpression(property, name));
        }
        query.setProjection(columns.toArray(new FeatureQuery.NamedExpression[0]));
        return query;
    }
}
