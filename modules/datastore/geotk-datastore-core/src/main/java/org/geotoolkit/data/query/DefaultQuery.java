/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;

import org.geotoolkit.factory.Hints;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default query implementation.
 *
 * @author Chris Holmes
 * @module pending
 */
class DefaultQuery implements Query {

    private final String language;
    private final Source source;
    private final Name[] properties;
    private final Integer maxFeatures;
    private final int startIndex;
    private final Filter filter;
    private final SortBy[] sortBy;
    private final Hints hints;
    private final CoordinateReferenceSystem crs;
    private final double[] resolution;

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    DefaultQuery(final Source name) {
        this(name,null);
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    DefaultQuery(final Source name, final Name[] attributs) {
        this(name,
                Filter.INCLUDE,
                attributs,
                null,
                null,
                0,
                null,
                null,
                null);
    }

    DefaultQuery(final Source source, final Filter filter, final Name[] attributs, final SortBy[] sort,
            final CoordinateReferenceSystem crs, final int startIndex, final Integer MaxFeature, 
            final double[] resolution, final Hints hints){

        if(source == null){
            throw new NullPointerException("Query source can not be null");
        }

        if(filter == null){
            throw new IllegalArgumentException("Query filter can not be null, did you mean Filter.INCLUDE ?");
        }

        this.source = source;
        this.filter = filter;
        this.properties = attributs;
        this.sortBy = sort;
        this.crs = crs;
        this.startIndex = startIndex;
        this.maxFeatures = MaxFeature;
        this.resolution = resolution;

        if(hints == null){
            this.hints = new Hints();
        }else{
            this.hints = hints;
        }

        this.language = GEOTK_QOM;
    }

    /**
     * A custom query statement in the given language.
     */
    DefaultQuery(final String language, final String statement) {
        this.language = language;
        this.source = new DefaultTextStatement(statement,null);
        this.properties = null;
        this.maxFeatures = null;
        this.startIndex = 0;
        this.filter = null;
        this.sortBy = null;
        this.hints = null;
        this.crs = null;
        this.resolution = null;
    }

    /**
     * Copy attributes from the given query
     * @param query : query to copy
     */
    DefaultQuery(final Query query) {
        this(query.getSource(),
             query.getFilter(),
             query.getPropertyNames(),
             query.getSortBy(),
             query.getCoordinateSystemReproject(),
             query.getStartIndex(),
             query.getMaxFeatures(),
             (query.getResolution()==null)?null:query.getResolution().clone(),
             query.getHints());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLanguage(){
        return language;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Source getSource(){
        return source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getTypeName() {
        if(source instanceof Selector){
            return ((Selector)source).getFeatureTypeName();
        }else{
            throw new IllegalStateException("Query getTypeName can only be called " +
                    "when query is simple (only one selector).");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSimple() {
        return source instanceof Selector;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name[] getPropertyNames() {
        return properties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean retrieveAllProperties() {
        return properties == null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getMaxFeatures() {
        return this.maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder returnString = new StringBuilder("Query:");

        returnString.append("\n   feature type: ").append(source);

        if (filter != null) {
            returnString.append("\n   filter: ").append(filter.toString());
        }

        returnString.append("\n   [properties: ");

        if ((properties == null) || (properties.length == 0)) {
            returnString.append(" ALL ]");
        } else {
            for (int i = 0; i < properties.length; i++) {
                returnString.append(properties[i]);

                if (i < (properties.length - 1)) {
                    returnString.append(", ");
                }
            }

            returnString.append("]");
        }

        if (sortBy != null && sortBy.length > 0) {
            returnString.append("\n   [sort by: ");
            for (int i = 0; i < sortBy.length; i++) {
                returnString.append(sortBy[i].getPropertyName().getPropertyName());
                returnString.append(" ");
                returnString.append(sortBy[i].getSortOrder().name());

                if (i < (sortBy.length - 1)) {
                    returnString.append(", ");
                }
            }

            returnString.append("]");
        }

        return returnString.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SortBy[] getSortBy() {
        return sortBy;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Hints getHints() {
        return hints;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateSystemReproject() {
        return crs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double[] getResolution() {
        return resolution;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultQuery other = (DefaultQuery) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (!Arrays.deepEquals(this.properties, other.properties)) {
            return false;
        }
        if (this.maxFeatures != other.maxFeatures && (this.maxFeatures == null || !this.maxFeatures.equals(other.maxFeatures))) {
            return false;
        }
        if (this.startIndex != other.startIndex) {
            return false;
        }
        if (this.filter != other.filter && (this.filter == null || !this.filter.equals(other.filter))) {
            return false;
        }
        if (!Arrays.deepEquals(this.sortBy, other.sortBy)) {
            return false;
        }
        if (this.hints != other.hints && (this.hints == null || !this.hints.equals(other.hints))) {
            return false;
        }
        if (this.crs != other.crs && (this.crs == null || !this.crs.equals(other.crs))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.properties);
        hash = 83 * hash + (this.maxFeatures != null ? this.maxFeatures.hashCode() : 0);
        hash = 83 * hash + this.startIndex;
        hash = 83 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.sortBy);
        hash = 83 * hash + (this.hints != null ? this.hints.hashCode() : 0);
        hash = 83 * hash + (this.crs != null ? this.crs.hashCode() : 0);
        return hash;
    }

}
