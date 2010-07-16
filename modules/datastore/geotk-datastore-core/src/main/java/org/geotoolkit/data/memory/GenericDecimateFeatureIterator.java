/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.memory;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.geometry.jts.decimation.GeometryDecimator;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Basic support for a FeatureIterator that decimate the geometry attribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericDecimateFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    protected final R iterator;
    protected final GeometryDecimator decimator;

    /**
     * Creates a new instance of GenericDecimateFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param decimator the geometry decimator to use
     */
    private GenericDecimateFeatureIterator(final R iterator, GeometryDecimator decimator) {
        this.iterator = iterator;
        this.decimator = decimator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        final Feature next = iterator.next();

        final Collection<Property> properties = new ArrayList<Property>();
        for(Property prop : next.getProperties()){
            if(prop instanceof GeometryAttribute){
                final GeometryAttribute geoAtt = (GeometryAttribute) prop;
                Object value = prop.getValue();
                if(value != null){
                    //decimate the geometry
                    value = decimator.decimate((Geometry) value);
                    GeometryDescriptor desc = geoAtt.getDescriptor();
                    prop = FF.createGeometryAttribute(value, desc, null, desc.getCoordinateReferenceSystem());
                }
            }
            properties.add(prop);
        }
        return (F) FF.createFeature(properties, next.getType(), next.getIdentifier().getID());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return iterator.hasNext();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(' ').append(decimator);
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureReader with a decimator.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericDecimateFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericDecimateFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericDecimateFeatureReader(R reader, GeometryDecimator decimator){
            super(reader,decimator);
        }

        @Override
        public T getFeatureType() {
            return (T) iterator.getFeatureType();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Wrap a FeatureReader with a decimator.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T, F> wrap(
            FeatureReader<T, F> reader, GeometryDecimator decimator) {
        final GeometryDescriptor desc = reader.getFeatureType().getGeometryDescriptor();
        if (desc != null) {
            return new GenericDecimateFeatureReader(reader, decimator);
        } else {
            return reader;
        }
    }

}
