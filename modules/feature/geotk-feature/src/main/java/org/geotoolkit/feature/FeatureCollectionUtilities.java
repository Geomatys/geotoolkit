/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.collection.FeatureCollection;

import org.geotoolkit.feature.collection.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class FeatureCollectionUtilities {

    protected FeatureCollectionUtilities(){}

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection() {
        return new DefaultFeatureCollection(null, null);
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection(final String id) {
        return new DefaultFeatureCollection(id, null);
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection(final String id, final SimpleFeatureType ft) {
        return new DefaultFeatureCollection(id, ft);
    }

    /**
     * Copies the provided features into a FeatureCollection.
     * <p>
     * Often used when gathering features for FeatureStore:<pre><code>
     * featureStore.addFeatures( DataUtilities.collection(array));
     * </code></pre>
     *
     * @param features Array of features
     * @return FeatureCollection
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> collection(final SimpleFeature[] features) {
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
        final int length = features.length;
        for (int i = 0; i < length; i++) {
            collection.add(features[i]);
        }
        return collection;
    }

    /**
     * Copies the provided features into a FeatureCollection.
     * <p>
     * Often used when gathering a FeatureCollection<SimpleFeatureType, SimpleFeature> into memory.
     *
     * @param featureCollection the features to add to a new feature collection.
     * @return FeatureCollection
     */
    public static DefaultFeatureCollection collection(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        return new DefaultFeatureCollection(featureCollection);
    }

    /**
     * Copies the feature ids from each and every feature into a set.
     * <p>
     * This method can be slurp an in memory record of the contents of a
     * @param featureCollection
     * @return set of all ids
     */
    public static Set<String> fidSet(final FeatureCollection<?, ?> featureCollection) {
        final HashSet<String> fids = new HashSet<String>();
        try {
            featureCollection.accepts(new FeatureVisitor() {

                @Override
                public void visit(Feature feature) {
                    fids.add(feature.getIdentifier().getID());
                }
            }, null);
        } catch (IOException ignore) {
        }
        return fids;
    }

    /**
     * Copies the provided features into a FeatureCollection.
     * <p>
     * Often used when gathering a FeatureCollection<SimpleFeatureType, SimpleFeature> into memory.
     *
     * @param list features to add to a new FeatureCollection
     * @return FeatureCollection
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> collection(final List<SimpleFeature> list) {
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
        for (SimpleFeature feature : list) {
            collection.add(feature);
        }
        return collection;
    }

    /**
     * Copies the provided features into a FeatureCollection.
     * <p>
     * Often used when gathering features for FeatureStore:<pre><code>
     * featureStore.addFeatures( DataUtilities.collection(feature));
     * </code></pre>
     *
     * @param feature a feature to add to a new collection
     * @return FeatureCollection
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> collection(final SimpleFeature feature) {
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
        collection.add(feature);
        return collection;
    }

    /**
     * Copies the provided reader into a FeatureCollection, reader will be closed.
     * <p>
     * Often used when gathering features for FeatureStore:<pre><code>
     * featureStore.addFeatures( DataUtilities.collection(reader));
     * </code></pre>
     *
     * @return FeatureCollection
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> collection(
            final FeatureReader<SimpleFeatureType, SimpleFeature> reader) throws IOException
    {
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
        try {
            while (reader.hasNext()) {
                try {
                    collection.add(reader.next());
                } catch (NoSuchElementException e) {
                    throw (IOException) new IOException("EOF").initCause(e);
                } catch (IllegalAttributeException e) {
                    throw (IOException) new IOException().initCause(e);
                }
            }
        } finally {
            reader.close();
        }
        return collection;
    }

    /**
     * Copies the provided reader into a FeatureCollection, reader will be closed.
     * <p>
     * Often used when gathering features for FeatureStore:<pre><code>
     * featureStore.addFeatures( DataUtilities.collection(reader));
     * </code></pre>
     *
     * @return FeatureCollection
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> collection(
            final FeatureIterator<SimpleFeature> reader) throws IOException
    {
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
        try {
            while (reader.hasNext()) {
                try {
                    collection.add(reader.next());
                } catch (NoSuchElementException e) {
                    throw (IOException) new IOException("EOF").initCause(e);
                }
            }
        } finally {
            reader.close();
        }
        return collection;
    }


}
