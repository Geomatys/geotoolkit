/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.coverage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;

/**
 * Iterator on {@link Envelope} which find all possible dimension combinations upper to 2 dimensions. 
 *
 * @author Cédric Briançon (Geomatys).
 * @author Johann Sorel    (Geomatys).
 * @author Remi Marechal   (Geomatys).
 * @module pending
 */
public class CombineIterator implements Iterator<Envelope> {
        private final List<List<Comparable>> values;
        private final int[] positions;
        private final GeneralEnvelope baseEnvelope;
        private boolean finish = false;

        public CombineIterator(final GeneralEnvelope baseEnvelope) {
            
            final CoordinateReferenceSystem crs = baseEnvelope.getCoordinateReferenceSystem();
            final CoordinateSystem cs = crs.getCoordinateSystem();
            // Stores additional coordinate system axes, to know how many pyramids should be created
            final List<List<Comparable>> possibilities = new ArrayList<List<Comparable>>();

            final int nbdim = cs.getDimension();
            for (int i = 2; i < nbdim; i++) {
                final CoordinateSystemAxis axis = cs.getAxis(i);
                if (axis instanceof DiscreteCoordinateSystemAxis) {
                    final DiscreteCoordinateSystemAxis daxis = (DiscreteCoordinateSystemAxis) axis;
                    final List<Comparable> values = new ArrayList<Comparable>();
                    possibilities.add(values);
                    final int nbval = daxis.length();
                    for (int k = 0; k < nbval; k++) {
                        final Comparable c = daxis.getOrdinateAt(k);
                        values.add(c);
                    }
                }
            }
            this.values       = possibilities;
            this.positions    = new int[possibilities.size()];
            this.baseEnvelope = baseEnvelope;
        }
        
        /**
         * Defines an iterator on given values with the given base envelope.
         *
         * @param values Values to iterate. Must not be {@code null}.
         * @param baseEnvelope Base envelope. Must not be {@code null}.
         */
        public CombineIterator(final List<List<Comparable>> values, final GeneralEnvelope baseEnvelope) {
            this.values       = values;
            this.positions    = new int[values.size()];
            this.baseEnvelope = baseEnvelope;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Envelope next() {
            if (finish) {
                return null;
            }

            for (int i = 0; i < positions.length; i++) {
                final Comparable c = values.get(i).get(positions[i]);
                Number n;
                if(c instanceof Number){
                    n = (Number) c;
                }else if(c instanceof Date){
                    n = ((Date)c).getTime();
                    //transform correctly value, unit type might have changed.
                    final CoordinateReferenceSystem baseCRS = DefaultTemporalCRS.JAVA;
                    final CoordinateReferenceSystem targetCRS = ((CompoundCRS)baseEnvelope.getCoordinateReferenceSystem()).getComponents().get(1+i);

                    //try to convert from one axis to the other
                    try{
                        final MathTransform trs = CRS.findMathTransform(baseCRS, targetCRS);
                        final double[] bv = new double[]{n.doubleValue()};
                        trs.transform(bv, 0, bv, 0, 1);
                        n = bv[0];
                    }catch(Exception ex){
                        throw new IllegalStateException(ex.getMessage(), ex);
                    }

                }else{
                    throw new IllegalStateException("Comparable type not supported : "+ c, null);
                }

                baseEnvelope.setRange(2+i, n.doubleValue(), n.doubleValue());

                //prepare next iteration
                if (i == positions.length - 1) {
                    positions[i] = positions[i] + 1;
                }
            }

            //prepare next iteration
            for (int i = positions.length - 1; i >= 0; i--) {
                if (positions[i] >= values.get(i).size()) {
                    if (i == 0) {
                        finish = true;
                        break;
                    }
                    //increment previous, restart this level at zero
                    positions[i] = 0;
                    positions[i - 1]++;
                }
            }

            return baseEnvelope;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return !finish;
        }

        /**
         * Not implemented in this implementation.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
