/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.coverage.mathcalc;

import java.util.AbstractMap;
import java.util.Set;
import java.util.logging.Level;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.opengis.coverage.Coverage;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcCoverageEvaluator implements FillCoverage.SampleEvaluator {

    private final GeneralDirectPosition positionGeo;
    private final DynamicPick pick;
    private final Expression exp;

    public MathCalcCoverageEvaluator(MathCalcCoverageEvaluator eval) throws FactoryException {
        this.exp = eval.exp;
        this.positionGeo = eval.positionGeo.clone();
        this.pick = new DynamicPick(eval.pick.coverages, eval.pick.mapping, positionGeo);
    }

    public MathCalcCoverageEvaluator(Coverage[] coverages, String[] mapping,
            Expression exp, CoordinateReferenceSystem crs) throws FactoryException {
        // prepare dynamic pick object
        this.exp = exp;
        positionGeo = new GeneralDirectPosition(crs);
        pick = new DynamicPick(coverages, mapping, positionGeo);
    }

    @Override
    public void evaluate(DirectPosition position, double[] sampleBuffer) {
        //update pick object position before evaluation
        positionGeo.setLocation(position);
        sampleBuffer[0] = exp.evaluate(pick, Double.class);
    }

    @Override
    public FillCoverage.SampleEvaluator copy() throws FactoryException {
        return new MathCalcCoverageEvaluator(this);
    }

    private static class DynamicPick extends AbstractMap{

        private final Coverage[] coverages;
        private final String[] mapping;
        private final MathTransform[] baseToCoverage;
        private final GeneralDirectPosition[] coverageCoord;
        private final DirectPosition coord;
        private final double[] sampleBuffer;

        private DynamicPick(Coverage[] coverages, String[] mapping, DirectPosition coord) throws FactoryException{
            this.coverages = coverages;
            this.mapping = mapping;
            this.coord = coord;
            this.sampleBuffer = new double[coverages[0].getNumSampleDimensions()];
            baseToCoverage = new MathTransform[coverages.length];
            coverageCoord = new GeneralDirectPosition[coverages.length];
            for(int i=0;i<coverages.length;i++){
                baseToCoverage[i] = CRS.findOperation(coord.getCoordinateReferenceSystem(), coverages[i].getCoordinateReferenceSystem(), null).getMathTransform();
                coverageCoord[i] = new GeneralDirectPosition(coverages[i].getCoordinateReferenceSystem());
            }
        }

        @Override
        public Object get(Object key) {
            //search the coverage for given name
            final String name = String.valueOf(key);
            int index = -1;
            for(int i=0;i<mapping.length;i++){
                if(mapping[i].equals(name)){
                    index = i;
                    try {
                        baseToCoverage[i].transform(coord, coverageCoord[i]);
                    } catch (Exception ex) {
                        Logging.getLogger("org.geotoolkit.processing.coverage.mathcalc").log(Level.WARNING, ex.getMessage(), ex);
                        return Double.NaN;
                    }
                    break;
                }
            }

            if(index<0){
                // no coverage for this name
                return Double.NaN;
            }

            //find value at given coordinate
            coverages[index].evaluate(coverageCoord[index],sampleBuffer);
            return sampleBuffer[0];
        }

        @Override
        public Set entrySet() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}
