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
package org.geotoolkit.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.crs.ReprojectFeatureReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.store.DataFeatureCollection;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Generic "results" of a query, class.
 * <p>
 * Please optimize this class when use with your own content.
 * For example a "ResultSet" make a great cache for a JDBCDataStore,
 * a temporary copy of an original file may work for shapefile etc.
 * </p>
 *
 * @author Jody Garnett, Refractions Research
 * @module pending
 */
public class DefaultFeatureResults extends DataFeatureCollection {

    /**
     * Shared package logger
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data");
    /**
     * Query used to define this subset of features from the feature source
     */
    protected final Query query;
    /**
     * Feature source used to aquire features, note we are only a
     * "view" of this FeatureSource, its contents, transaction and events
     * need to be forwarded through this collection api to simplier code
     * such as renderers.
     */
    protected final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
    protected MathTransform transform;

    /**
     * FeatureResults query against featureSource.
     * <p>
     * Please note that is object will not be valid
     * after the transaction has closed.
     * </p>
     * <p>
     * Really? I think it would be, it would just reflect the
     * same query against the FeatureSource<SimpleFeatureType, SimpleFeature> using AUTO_COMMIT.
     * </p>
     *
     * @param source
     * @param query
     */
    public DefaultFeatureResults(final FeatureSource<SimpleFeatureType, SimpleFeature> source,
            final Query query) throws IOException {
        super(null, getSchemaInternal(source, query));
        this.featureSource = source;

        final SimpleFeatureType origionalType = source.getSchema();

        final Name typeName = origionalType.getName();
        if (typeName.equals(query.getTypeName())) {
            this.query = query;
        } else {
            throw new IllegalArgumentException("Query type name doesn't match this source name, query : "
                    + query.getTypeName() +" but source is : " + typeName );
        }

        if (origionalType.getGeometryDescriptor() == null) {
            return; // no transform needed
        }

        final CoordinateReferenceSystem cs = query.getCoordinateSystemReproject();
        final CoordinateReferenceSystem origionalCRS = origionalType.getGeometryDescriptor().getCoordinateReferenceSystem();
        
        if (cs != null && CRS.equalsIgnoreMetadata(cs, origionalCRS)) {
            try {
                transform = CRS.findMathTransform(origionalCRS, cs, true);
            } catch (FactoryException noTransform) {
                throw (IOException) new IOException("Could not reproject data to " + cs).initCause(noTransform);
            }
        }
    }

    static SimpleFeatureType getSchemaInternal(
            final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, final Query query) {
        final SimpleFeatureType origionalType = featureSource.getSchema();
        SimpleFeatureType schema = null;

        CoordinateReferenceSystem cs = query.getCoordinateSystemReproject();

        try {
            if (cs == null) {
                if (query.retrieveAllProperties()) { // we can use the origionalType as is
                    schema = featureSource.getSchema();
                } else {
                    schema = FeatureTypeUtilities.createSubType(featureSource.getSchema(), query.getPropertyNames());
                }
            } else {
                // we need to change the projection of the origional type
                schema = FeatureTypeUtilities.createSubType(origionalType, query.getPropertyNames(), 
                        cs, query.getTypeName().getLocalPart(), null);
            }
        } catch (SchemaException e) {
            // we were unable to create the schema requested!
            //throw new DataSourceException("Could not create schema", e);
            LOGGER.log(Level.WARNING, "Could not change projection to " + cs, e);
            schema = null; // client will notice something is amiss when getSchema() return null
        }

        return schema;
    }

    /**
     * FeatureSchema for provided query.
     *
     * <p>
     * If query.retrieveAllProperties() is <code>true</code> the FeatureSource
     * getSchema() will be returned.
     * </p>
     *
     * <p>
     * If query.getPropertyNames() is used to limit the result of the Query a
     * sub type will be returned based on FeatureSource.getSchema().
     * </p>
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws DataSourceException DOCUMENT ME!
     */
    @Override
    public SimpleFeatureType getSchema() {
        return super.getSchema();
    }

    /**
     * Returns transaction from FeatureSource<SimpleFeatureType, SimpleFeature> (if it is a FeatureStore), or
     * Transaction.AUTO_COMMIT if it is not.
     *
     * @return Transacstion this FeatureResults opperates against
     */
    protected Transaction getTransaction() {
        if (featureSource instanceof FeatureStore) {
            final FeatureStore<SimpleFeatureType, SimpleFeature> featureStore =
                    (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;

            return featureStore.getTransaction();
        } else {
            return Transaction.AUTO_COMMIT;
        }
    }

    /**
     * Retrieve a  FeatureReader<SimpleFeatureType, SimpleFeature> for this Query
     *
     * @return  FeatureReader<SimpleFeatureType, SimpleFeature> for this Query
     *
     * @throws IOException If results could not be obtained
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> reader() throws IOException {
        FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                ((DataStore) featureSource.getDataStore()).getFeatureReader(query,
                getTransaction());

        final int maxFeatures = query.getMaxFeatures();
        if (maxFeatures != Integer.MAX_VALUE) {
            reader = new MaxFeatureReader<SimpleFeatureType, SimpleFeature>(reader, maxFeatures);
        }
        if (transform != null) {
            reader = new ReprojectFeatureReader(reader, getSchema(), transform);
        }
        return reader;
    }

    /**
     * Retrieve a  FeatureReader<SimpleFeatureType, SimpleFeature> for the geometry attributes only, designed for bounds computation
     */
    protected FeatureReader<SimpleFeatureType, SimpleFeature> boundsReader() throws IOException {
        final List<String> attributes = new ArrayList<String>();
        final SimpleFeatureType schema = featureSource.getSchema();
        for (int i = 0; i < schema.getAttributeCount(); i++) {
            final AttributeDescriptor at = schema.getDescriptor(i);
            if (at instanceof DefaultGeometryDescriptor) {
                attributes.add(at.getLocalName());
            }
        }

        final QueryBuilder builder = new QueryBuilder();
        builder.copy(query);
        builder.setProperties(attributes.toArray(new String[attributes.size()]));
        final Query q = builder.buildQuery();
        final FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                ((DataStore) featureSource.getDataStore()).getFeatureReader(q, getTransaction());
        final int maxFeatures = query.getMaxFeatures();

        if (maxFeatures == Integer.MAX_VALUE) {
            return reader;
        } else {
            return new MaxFeatureReader<SimpleFeatureType, SimpleFeature>(reader, maxFeatures);
        }
    }

    /**
     * Returns the bounding box of this FeatureResults
     *
     * <p>
     * This implementation will generate the correct results from reader() if
     * the provided FeatureSource<SimpleFeatureType, SimpleFeature> does not provide an optimized result via
     * FeatureSource.getBounds( Query ).
     * </p>
     * If the feature has no geometry, then an empty envelope is returned.
     */
    @Override
    public JTSEnvelope2D getBounds() {
        JTSEnvelope2D bounds;

        try {
            bounds = featureSource.getBounds(query);
        } catch (IOException e1) {
            bounds = new JTSEnvelope2D((CoordinateReferenceSystem) null);
        }

        if (bounds == null) {
            try {
                SimpleFeature feature;
                bounds = new JTSEnvelope2D();

                final FeatureReader<SimpleFeatureType, SimpleFeature> reader = boundsReader();

                while (reader.hasNext()) {
                    feature = reader.next();
                    bounds.include(feature.getBounds());
                }

                reader.close();
            } catch (IllegalAttributeException e) {
                //throw new DataSourceException("Could not read feature ", e);
                bounds = new JTSEnvelope2D();
            } catch (IOException e) {
                bounds = new JTSEnvelope2D();
            }
        }

        return bounds;
    }

    /**
     * Number of Features in this query.
     *
     * <p>
     * This implementation will generate the correct results from reader() if
     * the provided FeatureSource<SimpleFeatureType, SimpleFeature> does not provide an optimized result via
     * FeatureSource.getCount( Query ).
     * </p>
     *
     *
     * @throws IOException If feature could not be read
     * @throws DataSourceException See IOException
     */
    @Override
    public int getCount() throws IOException {
        int count = featureSource.getCount(query);

        if (count != -1) {
            // optimization worked, return maxFeatures if count is
            // greater.
            final int maxFeatures = query.getMaxFeatures();
            return (count < maxFeatures) ? count : maxFeatures;
        }

        // Okay lets count the FeatureReader
        try {
            count = 0;

            final FeatureReader<SimpleFeatureType, SimpleFeature> reader = reader();

            for (; reader.hasNext(); count++) {
                reader.next();
            }

            reader.close();

            return count;
        } catch (IllegalAttributeException e) {
            throw new DataSourceException("Could not read feature ", e);
        }
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature> collection() throws IOException {
        try {
            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
            //Feature feature;
            final FeatureReader<SimpleFeatureType, SimpleFeature> reader = reader();
            //SimpleFeatureType type = reader.getFeatureType();
            while (reader.hasNext()) {
                collection.add(reader.next());
            }
            reader.close();

            return collection;
        } catch (IllegalAttributeException e) {
            throw new DataSourceException("Could not read feature ", e);
        }
    }
}
