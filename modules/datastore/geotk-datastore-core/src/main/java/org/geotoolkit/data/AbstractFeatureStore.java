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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.feature.collection.FeatureCollection;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * This is a starting point for providing your own FeatureStore implementation.
 *
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 */
public abstract class AbstractFeatureStore extends AbstractFeatureSource
        implements FeatureStore<SimpleFeatureType, SimpleFeature> {

    /**
     * Current Transaction this FeatureSource<SimpleFeatureType, SimpleFeature> is opperating against
     */
    protected Transaction transaction = Transaction.AUTO_COMMIT;

    public AbstractFeatureStore() {
        this(null);
    }

    /**
     * This constructors allows to set the supported hints 
     * @param hints
     */
    public AbstractFeatureStore(final Set hints) {
        super(hints);
    }

    /**
     * Retrieve the Transaction this FeatureSource<SimpleFeatureType, SimpleFeature> is opperating against.
     *
     * @return Transaction FeatureSource<SimpleFeatureType, SimpleFeature> is operating against
     */
    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTransaction(final Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null, did you mean Transaction.AUTO_COMMIT?");
        }
        this.transaction = transaction;
    }

    /**
     * Add Features from reader to this FeatureStore.
     * 
     * <p>
     * Equivelent to:
     * </p>
     * <pre><code>
     * Set set = new HashSet();
     * FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter( typeName, true, transaction );
     * Featrue feature, newFeature;
     * while( reader.hasNext() ){
     *    feature = reader.next();
     *    newFeature = writer.next();
     *    newFeature.setAttributes( feature.getAttribtues( null ) );
     *    writer.write();
     *    set.add( newfeature.getID() );
     * }
     * reader.close();
     * writer.close();
     * 
     * return set;
     * </code>
     * </pre>
     * 
     * <p>
     * (If you don't have a  FeatureReader<SimpleFeatureType, SimpleFeature> handy DataUtilities.reader() may be
     * able to help out)
     * </p>
     * 
     * <p>
     * Subclasses may override this method to perform the appropriate
     * optimization for this result.
     * </p>
     *
     * @param reader
     *
     * @return The Set of FeatureIDs added
     *
     * @throws IOException If we encounter a problem encounter writing content
     * @throws DataSourceException See IOException
     *
     * @see org.geotoolkit.data.FeatureStore#addFeatures(org.geotoolkit.data.FeatureReader)
     */
    @Override
    public List<FeatureId> addFeatures(final FeatureReader<SimpleFeatureType, SimpleFeature> reader) throws IOException {
        final List<FeatureId> addedFids = new ArrayList<FeatureId>();
        final String typeName = getSchema().getTypeName();
        final FeatureWriter<SimpleFeatureType, SimpleFeature> writer = getDataStore().getFeatureWriterAppend(typeName, getTransaction());

        try {
            while (reader.hasNext()) {
                final SimpleFeature feature;
                try {
                    feature = reader.next();
                } catch (Exception e) {
                    throw new DataSourceException("Could not add Features, problem with provided reader", e);
                }

                final SimpleFeature newFeature = (SimpleFeature) writer.next();

                try {
                    newFeature.setAttributes(feature.getAttributes());
                } catch (Exception writeProblem) {
                    throw new DataSourceException("Could not create " + typeName + " out of provided feature: " + feature.getID(), writeProblem);
                }

                writer.write();
                addedFids.add(newFeature.getIdentifier());
            }
        } finally {
            reader.close();
            writer.close();
        }

        return addedFids;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection)
            throws IOException {
        final List<FeatureId> addedFids = new ArrayList<FeatureId>();
        final String typeName = getSchema().getTypeName();
        final FeatureWriter<SimpleFeatureType, SimpleFeature> writer = getDataStore().getFeatureWriterAppend(typeName, getTransaction());

        final Iterator<SimpleFeature> iterator = collection.iterator();
        try {

            while (iterator.hasNext()) {
                final SimpleFeature feature = iterator.next();
                final SimpleFeature newFeature = writer.next();
                try {
                    newFeature.setAttributes(feature.getAttributes());
                } catch (Exception writeProblem) {
                    throw new DataSourceException("Could not create " + typeName + " out of provided feature: " + feature.getID(), writeProblem);
                }

                writer.write();
                addedFids.add(newFeature.getIdentifier());
            }
        } finally {
            collection.close(iterator);
            writer.close();
        }
        return addedFids;
    }

    /**
     * Modifies features matching <code>filter</code>.
     *
     * <p>
     * Equivelent to:
     * </p>
     * <pre><code>
     * FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter( typeName, filter, transaction );
     * while( writer.hasNext() ){
     *    feature = writer.next();
     *    feature.setAttribute( type.getName(), value );
     *    writer.write();
     * }
     * writer.close();
     * </code>
     * </pre>
     *
     * <p>
     * Subclasses may override this method to perform the appropriate
     * optimization for this result.
     * </p>
     *
     * @param type Attribute to modify
     * @param value Modification being made to type
     * @param filter Identifies features to modify
     *
     * @throws IOException If modification could not be made
     */
    @Override
    public void updateFeatures(final AttributeDescriptor type, final Object value, final Filter filter)
            throws IOException {
        updateFeatures(new AttributeDescriptor[]{type}, new Object[]{value}, filter);
    }

    /**
     * Modifies features matching <code>filter</code>.
     *
     * <p>
     * Equivalent to:
     * </p>
     * <pre><code>
     * FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter( typeName, filter, transaction );
     * Feature feature;
     * while( writer.hasNext() ){
     *    feature = writer.next();
     *    feature.setAttribute( type[0].getName(), value[0] );
     *    feature.setAttribute( type[1].getName(), value[1] );
     *    ...
     *    feature.setAttribute( type[N].getName(), value[N] );
     *    writer.write();
     * }
     * writer.close();
     * </code>
     * </pre>
     *
     * <p>
     * Subclasses may override this method to perform the appropriate
     * optimization for this result.
     * </p>
     *
     * @param type Attributes to modify
     * @param value Modifications being made to type
     * @param filter Identifies features to modify
     *
     * @throws IOException If we could not modify Feature
     * @throws DataSourceException See IOException
     */
    @Override
    public void updateFeatures(final AttributeDescriptor[] type, final Object[] value,
            final Filter filter) throws IOException {
        final String typeName = getSchema().getTypeName();
        final FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                getDataStore().getFeatureWriter(typeName, filter, getTransaction());

        try {
            while (writer.hasNext()) {
                final SimpleFeature feature = writer.next();

                for (int i=0; i<type.length; i++) {
                    try {
                        feature.setAttribute(type[i].getLocalName(), value[i]);
                    } catch (Exception e) {
                        throw new DataSourceException(
                                "Could not update feature " + feature.getID() + " with " + type[i].getLocalName() + "=" + value[i], e);
                    }
                }

                writer.write();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Removes features indicated by provided filter.
     * 
     * <p>
     * Equivelent to:
     * </p>
     * <pre><code>
     * FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter( typeName, filter, transaction );
     * Feature feature;
     * while( writer.hasNext() ){
     *    feature = writer.next();
     *    writer.remove();
     * }
     * writer.close();
     * </code>
     * </pre>
     * 
     * <p>
     * Subclasses may override this method to perform the appropriate
     * optimization for this result.
     * </p>
     *
     * @param filter Identifies features to remove
     *
     * @throws IOException
     */
    @Override
    public void removeFeatures(final Filter filter) throws IOException {
        final String typeName = getSchema().getTypeName();
        final FeatureWriter<SimpleFeatureType, SimpleFeature> writer = 
                getDataStore().getFeatureWriter(typeName, filter, getTransaction());

        try {
            while (writer.hasNext()) {
                writer.next();
                writer.remove();
            }
        } finally {
            writer.close();
        }
    }

}
