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
import java.util.Collection;
import java.util.NoSuchElementException;

import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.collection.FeatureIterator;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Utility functions for use when implementing working with data classes.
 * <p>
 * TODO: Move FeatureType manipulation to feature package
 * </p>
 * @author Jody Garnett, Refractions Research
 * @module pending
 */
public final class DataUtilities extends FeatureCollectionUtilities {

    /**
     * Creates a  FeatureReader<SimpleFeatureType, SimpleFeature> for testing.
     *
     * @param features Array of features
     *
     * @return  FeatureReader<SimpleFeatureType, SimpleFeature> spaning provided feature array
     *
     * @throws IOException If provided features Are null or empty
     * @throws NoSuchElementException DOCUMENT ME!
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(final SimpleFeature[] features)
            throws IOException {
        if ((features == null) || (features.length == 0)) {
            throw new IOException("Provided features where empty");
        }

        return new FeatureReader<SimpleFeatureType, SimpleFeature>() {

            SimpleFeature[] array = features;
            int offset = -1;

            @Override
            public SimpleFeatureType getFeatureType() {
                return features[0].getFeatureType();
            }

            @Override
            public SimpleFeature next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more features");
                }

                return array[++offset];
            }

            @Override
            public boolean hasNext() {
                return (array != null) && (offset < (array.length - 1));
            }

            @Override
            public void close() {
                array = null;
                offset = -1;
            }
        };
    }

    public static FeatureSource<SimpleFeatureType, SimpleFeature> source(final SimpleFeature[] featureArray) {
        final SimpleFeatureType featureType;

        if ((featureArray == null) || (featureArray.length == 0)) {
            featureType = FeatureTypeUtilities.EMPTY;
        } else {
            featureType = featureArray[0].getFeatureType();
        }

        final DataStore arrayStore = new AbstractDataStore() {

            @Override
            public String[] getTypeNames() {
                return new String[]{featureType.getTypeName()};
            }

            @Override
            public SimpleFeatureType getSchema(final String typeName)
                    throws IOException {
                if ((typeName != null) && typeName.equals(featureType.getTypeName())) {
                    return featureType;
                }

                throw new IOException(typeName + " not available");
            }

            @Override
            public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(final String typeName)
                    throws IOException {
                return reader(featureArray);
            }
        };

        try {
            return arrayStore.getFeatureSource(arrayStore.getTypeNames()[0]);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something is wrong with the geotools code, " + "this exception should not happen", e);
        }
    }

    public static FeatureSource<SimpleFeatureType, SimpleFeature> source(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        if (collection == null) {
            throw new NullPointerException();
        }

        final DataStore store = MemoryDataStore.create(collection);

        try {
            return store.getFeatureSource(store.getTypeNames()[0]);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something is wrong with the geotools code, " + "this exception should not happen", e);
        }
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> results(final SimpleFeature[] featureArray) {
        return results(collection(featureArray));
    }

    /**
     * Returns collection if non empty.
     *
     * @param collection
     *
     * @return provided collection
     *
     * @throws IOException Raised if collection was empty
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> results(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        if (collection.size() == 0) {
            //throw new IOException("Provided collection was empty");
        }
        return collection;
    }

    /**
     * Adapt a collection to a reader for use with FeatureStore.setFeatures( reader ).
     *
     * @param collection Collection of SimpleFeature
     *
     * @return FeatureRedaer over the provided contents
     * @throws IOException IOException if there is any problem reading the content.
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(final Collection<SimpleFeature> collection)
            throws IOException {
        return reader((SimpleFeature[]) collection.toArray(
                new SimpleFeature[collection.size()]));
    }

    /**
     * Adapt a collection to a reader for use with FeatureStore.setFeatures( reader ).
     *
     * @param collection Collection of SimpleFeature
     *
     * @return FeatureRedaer over the provided contents
     * @throws IOException IOException if there is any problem reading the content.
     */
    public static FeatureReader<SimpleFeatureType, SimpleFeature> reader(
            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws IOException {
        return reader((SimpleFeature[]) collection.toArray(new SimpleFeature[collection.size()]));
    }

    /**
     * Manually calculates the bounds of a feature collection.
     * @param collection
     * @return Envelope
     */
    public static Envelope bounds(final FeatureCollection<? extends FeatureType, ? extends Feature> collection) {
        final FeatureIterator<? extends Feature> i = collection.features();
        try {
            final JTSEnvelope2D bounds = new JTSEnvelope2D(collection.getSchema().getCoordinateReferenceSystem());
            if (!i.hasNext()) {
                bounds.setToNull();
                return bounds;
            }

            bounds.init(((SimpleFeature) i.next()).getBounds());
            return bounds;
        } finally {
            i.close();
        }
    }

    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrapToReader(T type, FeatureIterator<F> ite){
        return new WrappedIterator<T, F>(type, ite);
    }

    private static class WrappedIterator<T extends FeatureType, F extends Feature> implements FeatureReader<T,F>{

        private final FeatureIterator<F> delegate;
        private final T schema;

        public WrappedIterator(final T featureType, final FeatureIterator<F> features) {
            if (featureType == null) {
                throw new NullPointerException("Feature type can not be null");
            }
            if (features == null) {
                throw new NullPointerException("Feature iterator can not be null");
            }
            this.schema = featureType;
            this.delegate = features;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public T getFeatureType() {
            return schema;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public F next() {
            return delegate.next();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean hasNext() throws IOException {
            return delegate.hasNext();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void close() throws IOException {
            delegate.close();
        }

    }

}
