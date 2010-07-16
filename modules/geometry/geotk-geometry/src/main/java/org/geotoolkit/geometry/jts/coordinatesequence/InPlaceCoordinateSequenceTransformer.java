/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.geotoolkit.geometry.jts.transform.CoordinateSequenceTransformer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;

/**
 * A JTS CoordinateSequenceTransformer which transforms the values in place.
 * <p>
 * Paragraph ...
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example:<pre><code>
 * InPlaceCoordinateSequenceTransformer x = new InPlaceCoordinateSequenceTransformer( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author jeichar
 * @module pending
 * @since 0.6.0
 */
public class InPlaceCoordinateSequenceTransformer implements CoordinateSequenceTransformer {

    private MathTransform transform = null;

    public InPlaceCoordinateSequenceTransformer(MathTransform transform){

    }

    public void setTransform(MathTransform transform) {
        this.transform = transform;
    }

    public MathTransform getTransform() {
        return transform;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateSequence transform( CoordinateSequence cs) throws TransformException {
        if( cs instanceof PackedCoordinateSequence ){
            return transformInternal( (PackedCoordinateSequence) cs, transform);
        }
        throw new TransformException(cs.getClass().getName()+" is not a implementation that is known to be transformable in place");
    }

    FlyWeightDirectPosition start=new FlyWeightDirectPosition(2);
    private CoordinateSequence transformInternal( PackedCoordinateSequence sequence, MathTransform transform ) 
    throws TransformException{
        
        start.setSequence(sequence);   
        for(int i=0; i<sequence.size();i++ ){
            start.setOffset(i);
            try {
                transform.transform(start, start);
            } catch (MismatchedDimensionException e) {
                throw new TransformException( "", e);
            } 
        }
        return sequence;
    }
    
    private class FlyWeightDirectPosition implements DirectPosition {
        PackedCoordinateSequence sequence;
        int offset=0;
        private int dimension;
        
        /**
         * Construct <code>InPlaceCoordinateSequenceTransformer.FlyWeightDirectPosition</code>.
         *
         */
        public FlyWeightDirectPosition(int dim) {
            dimension=dim;
        }
        
        /**
         * @param offset The offset to set.
         */
        public void setOffset( int offset ) {
            this.offset = offset;
        }
        
        /**
         * @param sequence The sequence to set.
         */
        public void setSequence( PackedCoordinateSequence sequence ) {
            this.sequence = sequence;
        }
        
        /**
         * {@inheritDoc }
         */
        @Override
        public int getDimension() {
            return dimension;
        }

        /**
         * {@inheritDoc }
         */
        @Deprecated
        public double[] getCoordinates() {
            return getCoordinate();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double[] getCoordinate() {
            return new double[]{ sequence.getX(offset), sequence.getY(offset), sequence.getOrdinate(offset, CoordinateSequence.Z)};
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double getOrdinate( int arg0 ) throws IndexOutOfBoundsException {
            return sequence.getOrdinate(offset, arg0);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate( int arg0, double arg1 ) throws IndexOutOfBoundsException {
            sequence.setOrdinate(offset, arg0, arg1);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            //TODO implement method body
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FlyWeightDirectPosition clone() {
            throw new UnsupportedOperationException();
        }

        public DirectPosition getPosition() {
            return this;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public DirectPosition getDirectPosition() {
            return this;
        }
        
    }

}
