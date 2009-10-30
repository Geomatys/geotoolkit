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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;

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
public class DefaultQuery implements Query {

    /**
     * The properties to fetch
     */
    private String[] properties;
    /**
     * The maximum numbers of features to fetch
     */
    private int maxFeatures = Query.DEFAULT_MAX;
    private Integer startIndex = null;
    /**
     * The filter to constrain the request.
     */
    private Filter filter = Filter.INCLUDE;
    /**
     * The typeName to get
     */
    private String typeName;
    /**
     * The namespace to get
     */
    private URI namespace = Query.NO_NAMESPACE;
    /**
     * The handle associated with this query.
     */
    private String handle;
    /**
     * Coordinate System associated with this query
     */
    private CoordinateReferenceSystem coordinateSystem;
    /**
     * Reprojection associated associated with this query
     */
    private CoordinateReferenceSystem coordinateSystemReproject;
    /**
     * Sorting for the query
     */
    private SortBy[] sortBy;
    /**
     * The version according to WFS 1.0 and 1.1 specs
     */
    private String version;
    /**
     * The hints to be used during query execution
     */
    private Hints hints;

    /**
     * No argument constructor.
     */
    public DefaultQuery() {
        // no arg
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    public DefaultQuery(final String typeName) {
        this(typeName, Filter.INCLUDE);
    }

    /**
     * Constructor with typeName and filter.  Note that current datasource
     * implementations only have one type per datasource, so the typeName
     * field will likely be ignored.
     *
     * @param typeName the name of the featureType to retrieve.
     * @param filter the OGC filter to constrain the request.
     */
    public DefaultQuery(final String typeName, final Filter filter) {
        this(typeName, filter, Query.ALL_NAMES);
    }

    /**
     * Constructor that sets the filter and properties
     * @param typeName
     *
     * @param filter the OGC filter to constrain the request.
     * @param properties an array of the properties to fetch.
     */
    public DefaultQuery(final String typeName, final Filter filter, final String[] properties) {
        this(typeName, null, filter, Query.DEFAULT_MAX, properties, null);
    }

    /**
     * Constructor that sets all fields.
     *
     * @param typeName the name of the featureType to retrieve.
     * @param filter the OGC filter to constrain the request.
     * @param maxFeatures the maximum number of features to be returned.
     * @param propNames an array of the properties to fetch.
     * @param handle the name to associate with the query.
     */
    public DefaultQuery(final String typeName, final Filter filter, final int maxFeatures,
            final String[] propNames, final String handle) {
        this(typeName, null, filter, maxFeatures, propNames, handle);
    }

    /**
     * Constructor that sets all fields.
     *
     * @param typeName the name of the featureType to retrieve.
     * @param namespace Namespace for provided typeName, or null if unspecified
     * @param filter the OGC filter to constrain the request.
     * @param maxFeatures the maximum number of features to be returned.
     * @param propNames an array of the properties to fetch.
     * @param handle the name to associate with the query.
     */
    public DefaultQuery(final String typeName, final URI namespace, final Filter filter,
            final int maxFeatures, final String[] propNames, final String handle) {
        this.typeName = typeName;
        this.filter = filter;
        this.namespace = namespace;
        this.properties = propNames;
        this.maxFeatures = maxFeatures;
        this.handle = handle;
    }

    /**
     * Copy contructor, clones the state of a generic Query into a DefaultQuery
     * @param query
     */
    public DefaultQuery(final Query query) {
        this(query.getTypeName(), query.getNamespace(), query.getFilter(), query.getMaxFeatures(),
                query.getPropertyNames(), query.getHandle());
        this.sortBy = query.getSortBy();
        this.coordinateSystem = query.getCoordinateSystem();
        this.coordinateSystemReproject = query.getCoordinateSystemReproject();
        this.version = query.getVersion();
        this.hints = query.getHints();
        this.startIndex = query.getStartIndex();
    }

    /**
     * The property names is used to specify the attributes that should be
     * selected for the return feature collection.  If the property array is
     * null, then the datasource should return all available properties, its
     * full schema.  If an array of  specified then the full schema should be
     * used (all property names). The property names can be determined with a
     * getSchema call from the DataSource interface.
     *
     * <p>
     * This replaces our funky setSchema method of retrieving select
     * properties.  I think it makes it easier to understand how to get
     * certain properties out of the datasource, instead of having users get
     * the  schema and then compose a new schema using the attributes that
     * they want.  The old way was also giving me problems because I couldn't
     * have multiple object reuse the same datasource object, since some other
     * object could come along and change its schema, and would then return
     * the wrong properties.
     * </p>
     *
     * <p></p>
     *
     * @return the property names to be used in the returned FeatureCollection.
     */
    @Override
    public String[] getPropertyNames() {
        return properties;
    }

    /**
     * Sets the properties to retrieve from the db.  If the boolean to load all
     * properties is set to true then the AttributeTypes that are not in the
     * database's schema will just be filled with null values.
     *
     * @param propNames The names of attributes to load from the datasouce.
     */
    public void setPropertyNames(final String[] propNames) {
        this.properties = propNames;
    }

    /**
     * Sets the proper attributeTypes constructed from a schema and a  list of
     * propertyNames.
     *
     * @param propNames the names of the properties to check against the
     *        schema. If null then all attributes will be returned.  If a List
     *        of size 0 is used then only the featureIDs should be used.
     *
     * @todo REVISIT: This syntax is really obscure.  Consider having an fid or
     *       featureID propertyName that datasource implementors look for
     *       instead of looking to see if the list size is 0.
     */
    public void setPropertyNames(final List propNames) {
        if (propNames == null) {
            this.properties = null;

            return;
        }

        String[] stringArr = new String[propNames.size()];
        this.properties = (String[]) propNames.toArray(stringArr);
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
    public int getMaxFeatures() {
        return this.maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(final Integer startIndex) {
        if (startIndex != null && startIndex.intValue() < 0) {
            throw new IllegalArgumentException("startIndex shall be a positive integer: " + startIndex);
        }
        this.startIndex = startIndex;
    }

    /**
     * Sets the max features to retrieved by this query.
     *
     * @param maxFeatures the maximum number of features the getFeature call
     *        should return.
     */
    public void setMaxFeatures(final int maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Sets the filter to constrain the query.
     *
     * @param filter the OGC filter to limit the datasource getFeatures
     *        request.
     */
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    /**
     * The typeName attribute is used to indicate the name of the feature type
     * to be queried.
     *
     * <p>
     * The DataStore API does not assume one feature type per datastore.
     * It currently makes use of this field to to specify with each request
     * what type to get.
     * </p>
     * @return the name of the feature type to be returned with this query.
     */
    @Override
    public String getTypeName() {
        return this.typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getNamespace() {
        return namespace;
    }

    /**
     * Sets the typename.
     *
     * @param typeName the name of the featureType to retrieve.
     */
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    /**
     * Set the namespace of the type name.
     *
     * @param namespace namespace of the type name
     */
    public void setNamespace(final URI namespace) {
        this.namespace = namespace;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getHandle() {
        return this.handle;
    }

    /**
     * Sets a mnemonic name for the query request.
     *
     * @param handle the name to refer to this query.
     */
    public void setHandle(final String handle) {
        this.handle = handle;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * @see #getVersion()
     * @param version
     * @since 2.4
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Hashcode based on propertyName, maxFeatures and filter.
     *
     * @return hascode for filter
     */
    @Override
    public int hashCode() {
        final String[] n = getPropertyNames();

        return ((n == null) ? (-1)
                : ((n.length == 0) ? 0 : (n.length | n[0].hashCode()))) | getMaxFeatures() | ((getFilter() == null) ? 0 : getFilter().hashCode()) | ((getTypeName() == null) ? 0 : getTypeName().hashCode()) | ((getVersion() == null) ? 0 : getVersion().hashCode()) | ((getCoordinateSystem() == null) ? 0 : getCoordinateSystem().hashCode()) | ((getCoordinateSystemReproject() == null) ? 0 : getCoordinateSystemReproject().hashCode()) | getStartIndex();
    }

    /**
     * Equality based on propertyNames, maxFeatures, filter, typeName and
     * version.
     *
     * <p>
     * Changing the handle does not change the meaning of the Query.
     * </p>
     *
     * @param obj Other object to compare against
     *
     * @return <code>true</code> if <code>obj</code> matches this filter
     */
    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || !(obj instanceof Query)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Query other = (Query) obj;

        return Arrays.equals(getPropertyNames(), other.getPropertyNames()) && (retrieveAllProperties() == other.retrieveAllProperties()) && (getMaxFeatures() == other.getMaxFeatures()) && ((getFilter() == null) ? (other.getFilter() == null)
                : getFilter().equals(other.getFilter())) && ((getTypeName() == null) ? (other.getTypeName() == null)
                : getTypeName().equals(other.getTypeName())) && ((getVersion() == null) ? (other.getVersion() == null)
                : getVersion().equals(other.getVersion())) && ((getCoordinateSystem() == null) ? (other.getCoordinateSystem() == null)
                : getCoordinateSystem().equals(other.getCoordinateSystem())) && ((getCoordinateSystemReproject() == null) ? (other.getCoordinateSystemReproject() == null)
                : getCoordinateSystemReproject().equals(other.getCoordinateSystemReproject())) && (getStartIndex() == other.getStartIndex());
    }

    /**
     * Over ride of toString
     *
     * @return a string representation of this query object.
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("Query:");

        if (handle != null) {
            returnString.append(" [" + handle + "]");
        }

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
     * getCoordinateSystem purpose.
     * <p>
     * Description ...
     * </p>
     */
    @Override
    public CoordinateReferenceSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * getCoordinateSystemReproject purpose.
     * <p>
     * Description ...
     * </p>
     */
    @Override
    public CoordinateReferenceSystem getCoordinateSystemReproject() {
        return coordinateSystemReproject;
    }

    /**
     * setCoordinateSystem purpose.
     * <p>
     * Description ...
     * </p>
     * @param system
     */
    public void setCoordinateSystem(final CoordinateReferenceSystem system) {
        coordinateSystem = system;
    }

    /**
     * setCoordinateSystemReproject purpose.
     * <p>
     * Description ...
     * </p>
     * @param system
     */
    public void setCoordinateSystemReproject(final CoordinateReferenceSystem system) {
        coordinateSystemReproject = system;
    }

    /**
     * Retrive list of SortBy information.
     * <p>
     * Note we are using SortBy2, to be standards complient
     * you may limit yourself to to SortBy information.
     * </p>
     */
    @Override
    public SortBy[] getSortBy() {
        return sortBy;
    }

    /**
     * Sets the sort by information.
     *
     */
    public void setSortBy(final SortBy[] sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public Hints getHints() {
        if (hints == null) {
            hints = new Hints(Collections.EMPTY_MAP);
        }
        return hints;
    }

    /**
     * Sets the query hints
     * @param hints
     */
    public void setHints(Hints hints) {
        this.hints = hints;
    }
}
