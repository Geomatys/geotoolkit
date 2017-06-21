/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.internal.data;

import org.geotoolkit.feature.ReprojectFeatureType;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericReprojectFeatureIterator<R extends FeatureReader> implements FeatureReader{

    protected final R iterator;
    protected final FeatureType baseType;
    protected final ReprojectFeatureType targetType;
    protected final CoordinateReferenceSystem targetCRS;


    public GenericReprojectFeatureIterator(R iterator, final CoordinateReferenceSystem targetCRS) throws FactoryException {
        this.iterator = iterator;
        this.baseType = iterator.getFeatureType();
        this.targetCRS = targetCRS;

        if(!baseType.isAbstract()){
            targetType = new ReprojectFeatureType(baseType, targetCRS);
        }else{
            targetType = null;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType() {
        return targetType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        return iterator.hasNext();
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        final Feature next = iterator.next();
        final Feature f;

        if(targetType!=null){
            f = targetType.newInstance(next);
        }else{
            final ReprojectFeatureType targetType = new ReprojectFeatureType(next.getType(),targetCRS);
            f = targetType.newInstance(next);
        }

        return f;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    private static final class GenericReprojectFeatureCollection extends WrapFeatureCollection{

        private final CoordinateReferenceSystem targetCrs;
        private final ReprojectFeatureType targetType;

        private GenericReprojectFeatureCollection(final FeatureCollection original, final CoordinateReferenceSystem targetCrs){
            super(original);
            this.targetCrs = targetCrs;
            this.targetType = new ReprojectFeatureType(original.getFeatureType(), targetCrs);
        }

        @Override
        public FeatureType getFeatureType() {
            return targetType;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            FeatureIterator ite = getOriginalFeatureCollection().iterator(hints);
            if(!(ite instanceof FeatureReader)){
                ite = FeatureStreams.asReader(ite, targetType);
            }
            try {
                return wrap((FeatureReader) ite, targetCrs, hints);
            } catch (FactoryException ex) {
                throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
            }
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a reprojection.
     */
    public static FeatureReader wrap(final FeatureReader reader, final CoordinateReferenceSystem crs, final Hints hints) throws FactoryException {
        return new GenericReprojectFeatureIterator(reader, crs);
    }

    /**
     * Create a reproject FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final CoordinateReferenceSystem crs){
        return new GenericReprojectFeatureCollection(original, crs);
    }

}
