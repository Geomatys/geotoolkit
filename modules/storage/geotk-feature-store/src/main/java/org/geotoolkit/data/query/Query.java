/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 - 2010, Geomatys
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
import java.util.Date;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.factory.Hints;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * The query object is used by the Session.getFeatureCollection(Query) to
 * encapsulate a request. The query may regroup feature from several sources
 * into a single feature type.
 * </p>
 *
 * <p>
 * Cross source queries will slow down considerably if the used session
 * are different, so it is recommended to create a new source that combines
 * the datas you want before if you have performance needs.
 * </p>
 *
 * <p>
 * This class is the counterpart of javax.jcr.query.qom.QueryObjectModel
 * from JSR-283 (Java Content Repository 2).
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 * @author Chris Holmes
 * @version $Id$
 * @module
 */
public final class Query extends org.apache.sis.storage.Query {

    /**
     * Default GeotoolKit language used for querying databases.
     */
    public static final String GEOTK_QOM = "GEOTK-QOM";

    private final String typeName;
    private final String[] properties;
    private final Integer maxFeatures;
    private final int startIndex;
    private final Filter filter;
    private final SortBy[] sortBy;
    private final Hints hints;
    private final CoordinateReferenceSystem crs;
    private final double[] resolution;
    private final Object version;


    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    Query(final String typeName) {
        this(typeName,null);
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    Query(final String typeName, final String[] attributs) {
        this(typeName,
                Filter.INCLUDE,
                attributs,
                null,
                null,
                0,
                null,
                null,
                null,
                null);
    }

    Query(final String typeName, final Filter filter, final String[] attributs, final SortBy[] sort,
            final CoordinateReferenceSystem crs, final int startIndex, final Integer MaxFeature,
            final double[] resolution, final Object version, final Hints hints){

        ensureNonNull("query source", typeName);
        if(filter == null){
            throw new NullArgumentException("Query filter can not be null, did you mean Filter.INCLUDE ?");
        }

        this.typeName = typeName;
        this.filter = filter;
        this.properties = attributs;
        this.sortBy = sort;
        this.crs = crs;
        this.startIndex = startIndex;
        this.maxFeatures = MaxFeature;
        this.resolution = resolution;
        this.version = version;

        if(hints == null){
            this.hints = new Hints();
        }else{
            this.hints = hints;
        }

    }

    /**
     * Copy attributes from the given query
     * @param query : query to copy
     */
    Query(final Query query) {
        this(query.getTypeName(),
             query.getFilter(),
             query.getPropertyNames(),
             query.getSortBy(),
             query.getCoordinateSystemReproject(),
             query.getStartIndex(),
             query.getMaxFeatures(),
             (query.getResolution()==null)?null:query.getResolution().clone(),
             (query.getVersionDate()!=null)? query.getVersionDate() : query.getVersionLabel(),
             query.getHints());
    }

    /**
     * The typeName attribute is used to indicate the name of the feature type
     * to be queried. This value can not be return if the query affects several
     * feature source.
     *
     * @return the name of the feature type to be returned with this query.
     * @throws IllegalStateException if the query is not simple.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * The Filter can be used to define constraints on a query.
     * The default value is Filter.INCLUDE .
     *
     * @return The filter that defines constraints on the query, can not be null.
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * The properties array is used to specify the attributes that should be
     * selected for the return feature collection.
     *
     * <ul>
     * <li>
     * ALL_NAMES: <code>null</code><br>
     * If no properties are specified (getProperties returns ALL_NAMES or
     * null) then the full schema should  be used (all attributes).
     * </li>
     * <li>
     * NO_NAMES: <code>new String[0]</code><br>
     * If getProperties returns an array of size 0, then the datasource should
     * return features with no attributes, only their ids.
     * </li>
     * </ul>
     *
     * <p>
     * The available properties can be determined with a getSchema call from
     * the DataSource interface.  A datasource can use {@link
     * #retrieveAllProperties()} as a shortcut to determine if all its
     * available properties should be returned (same as checking to see if
     * getProperties is ALL_NAMES, but clearer)
     * </p>
     *
     * <p>
     * If properties that are not part of the datasource's schema are requested
     * then the datasource shall throw an exception.
     * </p>
     *
     * <p>
     * This replaces our funky setSchema method of retrieving select
     * properties.  It makes it easier to understand how to get certain
     * properties out of the datasource, instead of having users get the
     * schema and then compose a new schema using the attributes that they
     * want.  The old way had problems because one couldn't have multiple
     * object reuse the same datasource object, since some other object could
     * come along and change its schema, and would then return the wrong
     * properties.
     * </p>
     *
     * @return the attributes to be used in the returned FeatureCollection.
     *
     * @todo : make a FidProperties object, instead of an array size 0.
     *       I think Query.FIDS fills this role to some degree.
     *       Query.FIDS.equals( filter ) would meet this need?
     */
    public String[] getPropertyNames() {
        return properties;
    }

    /**
     * Convenience method to determine if the query should use the full schema
     * (all properties) of the data source for the features returned.  This
     * method is equivalent to if (query.getProperties() == null), but allows
     * for more clarity on the part of datasource implementors, so they do not
     * need to examine and use null values.  All Query implementations should
     * return true for this function if getProperties returns null.
     *
     * @return if all datasource attributes should be included in the schema of
     *         the returned FeatureCollection.
     */
    public boolean retrieveAllProperties() {
        return properties == null;
    }

    /**
     * Get the starting index. default value is 0.
     * Index start counting from 0 so if startIndex == 1 then
     * the first element should be skipped.
     *
     * @return starting index.
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * The optional maxFeatures can be used to limit the number of features
     * that a query request retrieves.  If no maxFeatures is specified then
     * all features should be returned.
     *
     * <p>
     * This is the only method that is not directly out of the Query element in
     * the WFS spec.  It is instead a part of a GetFeature request, which can
     * hold one or more queries.  But each of those in turn will need a
     * maxFeatures, so it is needed here.
     * </p>
     *
     * @return the max features the getFeature call should return.
     */
    public Integer getMaxFeatures() {
        return this.maxFeatures;
    }

    /**
     * Request data reprojection.
     *
     * <p>
     * Gets the coordinate System to reproject the data contained in the
     * backend feature store to.
     * </p>
     *
     * <p>
     * If the feature store can optimize the reprojection it should, if not then a
     * decorator on the reader should perform the reprojection on the fly.
     * </p>
     *
     * <p>
     * If the feature store has the wrong CS then {@link #getCoordinateSystem()} should be set to
     * the CS to be used, this will perform the reprojection on that.
     * </p>
     *
     * @return The coordinate system that Features from the datasource should
     *         be reprojected to.
     */
    public CoordinateReferenceSystem getCoordinateSystemReproject() {
        return crs;
    }

    /**
     * Set The wished resolution of the geometries.
     * Since there is no Envelope provided in the query like in CoverageReadParam
     * this resolution must be expressed in the native data coordinate reference system.
     *
     * @return resolution or null if no resolution provided.
     */
    public double[] getResolution() {
        return resolution;
    }

    /**
     * SortBy results according to indicated property and order.
     * <p>
     * SortBy is part of the Filter 1.1 specification, it is referenced
     * by WFS1.1 and Catalog 2.0.x specifications and is used to organize
     * results.
     * </p>
     * The SortBy's are ment to be applied in order:
     * <ul>
     * <li>SortBy( year, ascending )
     * <li>SortBy( month, decsending )
     * </ul>
     * Would produce something like: <pre><code>
     * [year=2002 month=4],[year=2002 month=3],[year=2002 month=2],
     * [year=2002 month=1],[year=2003 month=12],[year=2002 month=4],
     * </code></pre>
     * </p>
     * <p>
     *
     * SortBy should be considered at the same level of abstraction as Filter,
     * and like Filter you may sort using properties not listed in
     * getPropertyNames.
     * </p>
     *
     * <p>
     * At a technical level the interface SortBy2 is used to indicate the
     * additional requirements of a GeoToolkit implementation. The pure
     * WFS 1.1 specification itself is limited to SortBy.
     * </p>
     *
     * @return SortBy array or order of application
     */
    public SortBy[] getSortBy() {
        return sortBy;
    }

    /**
     * Requested version label of the features.
     * If the value is null the latest version is returned.
     * Mutualy excludive with VersionDate.
     * @return String, may be null.
     */
    public String getVersionLabel() {
        if(version instanceof String){
            return (String)version;
        }
        return null;
    }

    /**
     * Requested version date of the features.
     * If the value is null the latest version is returned.
     * Mutualy excludive with VersionLabel.
     * @return Date, may be null.
     */
    public Date getVersionDate() {
        if(version instanceof Date){
            return (Date)version;
        }
        return null;
    }

    /**
     * Specifies some hints to drive the query execution and results build-up.
     * Hints examples can be the GeometryFactory to be used, a generalization
     * distance to be applied right in the data store, to data store specific
     * things such as the fetch size to be used in JDBC queries.
     * The set of hints supported can be fetched by calling
     * {@link FeatureSource#getSupportedHints()}.
     * Depending on the actual values of the hints, the data store is free to ignore them.
     * No mechanism is in place, at the moment, to figure out which hints where
     * actually used during the query execution.
     * @return the Hints the data store should try to use when executing the query
     *         (eventually empty but never null).
     */
    public Hints getHints() {
        return hints;
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
        final Query other = (Query) obj;
        if (this.typeName != other.typeName && (this.typeName == null || !this.typeName.equals(other.typeName))) {
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
        hash = 83 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.properties);
        hash = 83 * hash + (this.maxFeatures != null ? this.maxFeatures.hashCode() : 0);
        hash = 83 * hash + this.startIndex;
        hash = 83 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 83 * hash + Arrays.deepHashCode(this.sortBy);
        hash = 83 * hash + (this.hints != null ? this.hints.hashCode() : 0);
        hash = 83 * hash + (this.crs != null ? this.crs.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder returnString = new StringBuilder("Query:");

        returnString.append("\n   feature type: ").append(typeName);

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

}
