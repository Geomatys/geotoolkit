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
 * The query object is used by the {@link FeatureSource#getFeatures()} method of
 * the DataSource interface, to encapsulate a request.  It defines which
 * feature type  to query, what properties to retrieve and what constraints
 * (spatial and non-spatial) to apply to those properties.  It is designed to
 * closesly match a WFS Query element of a <code>getFeatures</code> request.
 * The only difference is the addition of the maxFeatures element, which
 * allows more control over the features selected.  It allows a full
 * <code>getFeatures</code> request to properly control how many features it
 * gets from each query, instead of requesting and discarding when the max is
 * reached.
 *
 * @author Chris Holmes
 * @module pending
 */
class DefaultQuery implements Query {

    private final Name typeName;
    private final String[] properties;
    private final Integer maxFeatures;
    private final Integer startIndex;
    private final Filter filter;
    private final SortBy[] sortBy;
    private final Hints hints;
    private final CoordinateReferenceSystem crs;

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    DefaultQuery(final Name name) {
        this(name,null);
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    DefaultQuery(final Name name, String[] attributs) {
        this(name,
                Filter.INCLUDE,
                attributs,
                null,
                null,
                null,
                null,
                null);
    }

    DefaultQuery(final Name name, Filter filter, String[] attributs,
            SortBy[] sort, CoordinateReferenceSystem crs, Integer startIndex, Integer MaxFeature,Hints hints){

        if(name == null){
            throw new NullPointerException("Type name can not be null");
        }

        if(filter == null){
            throw new IllegalArgumentException("Filter can not be null, did you mean Filter.INCLUDE ?");
        }

        this.typeName = name;
        this.filter = filter;
        this.properties = attributs;
        this.sortBy = sort;
        this.crs = crs;
        this.startIndex = startIndex;
        this.maxFeatures = MaxFeature;

        if(hints == null){
            this.hints = new Hints();
        }else{
            this.hints = hints;
        }
    }

    /**
     * Copy attributs from the given query
     * @param query : query to copy
     */
    public DefaultQuery(final Query query) {
        this(query.getTypeName(), 
             query.getFilter(),
             query.getPropertyNames(),
             query.getSortBy(),
             query.getCoordinateSystemReproject(),
             query.getStartIndex(),
             query.getMaxFeatures(),
             query.getHints());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getPropertyNames() {
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
    public Integer getStartIndex() {
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
    public Name getTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("Query:");

        returnString.append("\n   feature type: " + typeName);

        if (filter != null) {
            returnString.append("\n   filter: " + filter.toString());
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultQuery other = (DefaultQuery) obj;
        if (this.typeName != other.typeName && (this.typeName == null || !this.typeName.equals(other.typeName))) {
            return false;
        }
        if (!Arrays.deepEquals(this.properties, other.properties)) {
            return false;
        }
        if (this.maxFeatures != other.maxFeatures && (this.maxFeatures == null || !this.maxFeatures.equals(other.maxFeatures))) {
            return false;
        }
        if (this.startIndex != other.startIndex && (this.startIndex == null || !this.startIndex.equals(other.startIndex))) {
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
        hash = 83 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.properties);
        hash = 83 * hash + (this.maxFeatures != null ? this.maxFeatures.hashCode() : 0);
        hash = 83 * hash + (this.startIndex != null ? this.startIndex.hashCode() : 0);
        hash = 83 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.sortBy);
        hash = 83 * hash + (this.hints != null ? this.hints.hashCode() : 0);
        hash = 83 * hash + (this.crs != null ? this.crs.hashCode() : 0);
        return hash;
    }


}
