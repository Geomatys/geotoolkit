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
package org.geotoolkit.storage.feature.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.feature.Features;
import org.apache.sis.filter.privy.XPath;
import org.apache.sis.storage.FeatureQuery;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Literal;
import org.opengis.filter.SortProperty;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

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
 * @deprecated use SIS FeatureQuery
 */
@Deprecated
public final class Query extends FeatureQuery {

    /**
     * Default GeotoolKit language used for querying databases.
     */
    public static final String GEOTK_QOM = "GEOTK-QOM";

    private String typeName;
    private Hints hints;
    private Object version;


    /**
     * Query with typeName.
     */
    public Query() {
        this.hints = new Hints();
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    public Query(final GenericName typeName) {
        this(typeName.toString(), null);
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    public Query(final String typeName) {
        this(typeName,null);
    }

    /**
     * Query with typeName.
     *
     * @param typeName the name of the featureType to retrieve
     */
    Query(final String typeName, final String[] attributs) {
        this(typeName,
                Filter.include(),
                attributs,
                null,
                0,
                -1,
                null,
                null,
                null);
    }

    Query(final String typeName, Filter filter, final String[] attributs, final SortProperty[] sort,
            final long startIndex, final long maxFeatures, Quantity<Length> linearResolution, final Object version, final Hints hints){

        ensureNonNull("query source", typeName);
        if (filter == null) {
            filter = Filter.include();
        }
        setSelection(filter);
        setOffset(startIndex);
        if (maxFeatures >= 0) {
            setLimit(maxFeatures);
        }
        setSortBy(sort);
        setLinearResolution(linearResolution);

        if (attributs != null && attributs.length > 0) {
            final FilterFactory ff = FilterUtilities.FF;
            final List<NamedExpression> columns = new ArrayList<>();
            for (String att : attributs) {
                columns.add(new NamedExpression(ff.property(att)));
            }
            setProjection(columns.toArray(new NamedExpression[0]));
        }
        this.typeName = typeName;
        this.version = version;

        if (hints == null) {
            this.hints = new Hints();
        } else {
            this.hints = hints;
        }
    }

    /**
     * Copy attributes from the given query
     * @param query : query to copy
     */
    Query(final Query query) {
        this(query.getTypeName(),
             query.getSelection(),
             query.getPropertyNames(),
             QueryUtilities.getSortProperties(query.getSortBy()),
             query.getOffset(),
             query.getLimit().orElse(-1),
             query.getLinearResolution(),
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

    public void setTypeName(final GenericName typeName) {
        this.typeName = typeName.toString();
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
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
        NamedExpression[] columns = getProjection();
        if (columns == null) return null;
        final String[] names = new String[columns.length];
        for (int i=0;i<names.length;i++) {
            names[i] = ((ValueReference)columns[i].expression).getXPath();
        }
        return names;
    }

    @Deprecated
    public void setProperties(final String[] attributs) {
        if (attributs != null && attributs.length > 0) {
            final FilterFactory ff = FilterUtilities.FF;
            final List<NamedExpression> columns = new ArrayList<>();
            for (String att : attributs) {
                columns.add(new NamedExpression(ff.property(att)));
            }
            setProjection(columns.toArray(new NamedExpression[0]));
        }
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
        return getProjection() == null;
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

    public void setVersionLabel(String label) {
        this.version = label;
    }

    public void setVersionDate(Date version) {
        this.version = version;
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

    public void setHints(final Hints hints) {
        this.hints = hints;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
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
        if (this.hints != other.hints && (this.hints == null || !this.hints.equals(other.hints))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 83 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 83 * hash + (this.hints != null ? this.hints.hashCode() : 0);
        return hash;
    }

    public void copy(final Query query){
        setSelection(query.getSelection());
        query.getLimit().ifPresent(this::setLimit);
        setProjection(query.getProjection());
        setSortBy(query.getSortBy());
        setOffset(query.getOffset());
        this.hints = query.getHints();
        this.typeName = query.getTypeName();
        this.version = query.getVersionDate();
        if (this.version == null) this.version = query.getVersionLabel();
    }

    /**
     * Create a simple query with only a filter parameter.
     *
     * @return Immutable query
     */
    public static Query filtered(final String name, final Filter filter){
        final Query query = new Query(name);
        query.setSelection(filter);
        return query;
    }

    /**
     * Create a simple query with columns which transform all geometric types to the given crs.
     */
    public static FeatureQuery reproject(FeatureType type, final CoordinateReferenceSystem crs) {
        final FilterFactory<Feature, Object, Object> ff = FilterUtilities.FF;
        final Literal<Feature, CoordinateReferenceSystem> crsLiteral = ff.literal(crs);

        final FeatureQuery query = new FeatureQuery();
        final List<FeatureQuery.NamedExpression> columns = new ArrayList<>();

        for (PropertyType pt : type.getProperties(true)) {
            final GenericName name = pt.getName();
            Expression<Feature,?> property = ff.property(XPath.toString(null, null, name.toString()));
            /*
             * Do not reproject links. If the link is referencing another property of this feature instance,
             * that property will already be reprojected by this loop. By skipping links, we avoid to project
             * the same geometry twice. It also avoids complication in the code creating features with a subset
             * of the properties.
             */
            if (Features.getLinkTarget(pt).isEmpty()) {
                IdentifiedType result = pt;
                while (result instanceof Operation) {
                    // Unroll operation
                    result = ((Operation) result).getResult();
                }
                if (result instanceof AttributeType<?> at) {
                    if (Geometry.class.isAssignableFrom(at.getValueClass())) {
                        property = ff.function("ST_Transform", property, crsLiteral);
                    }
                }
            }
            columns.add(new FeatureQuery.NamedExpression(property, name));
        }
        query.setProjection(columns.toArray(new FeatureQuery.NamedExpression[0]));
        return query;
    }
}
