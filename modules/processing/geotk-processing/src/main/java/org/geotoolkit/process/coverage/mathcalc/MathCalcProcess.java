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

package org.geotoolkit.process.coverage.mathcalc;

import java.util.AbstractMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.WrapFilterFactory2;
import static org.geotoolkit.parameter.Parameters.value;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.opengis.coverage.Coverage;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcProcess extends AbstractProcess {

    public MathCalcProcess(Coverage[] inCoverages, String inFormula, String[] inMapping, CoverageReference outCoverage){
        this(toParameters(inCoverages, inFormula, inMapping, outCoverage));
    }
    
    public MathCalcProcess(ParameterValueGroup params) {
        super(MathCalcDescriptor.INSTANCE, params);
    }

    private static ParameterValueGroup toParameters(Coverage[] inCoverages, String inFormula, String[] inMapping, CoverageReference outCoverage){
        final ParameterValueGroup params = MathCalcDescriptor.INSTANCE.getInputDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, "inCoverages").setValue(inCoverages);
        ParametersExt.getOrCreateValue(params, "inFormula").setValue(inFormula);
        ParametersExt.getOrCreateValue(params, "inMapping").setValue(inMapping);
        ParametersExt.getOrCreateValue(params, "inResultCoverage").setValue(outCoverage);
        return params;
    }
    
    @Override
    protected void execute() throws ProcessException {
        final Coverage[] inCoverages = value(MathCalcDescriptor.IN_COVERAGES, inputParameters);
        final String inFormula = value(MathCalcDescriptor.IN_FORMULA, inputParameters);
        final String[] inMapping = value(MathCalcDescriptor.IN_MAPPING, inputParameters);
        final CoverageReference outRef = value(MathCalcDescriptor.IN_RESULT_COVERAGE, inputParameters);
        
        final GeneralGridGeometry gg;
        final GridCoverageReader outReader;
        try {
            outReader = outRef.acquireReader();
            gg = outReader.getGridGeometry(outRef.getImageIndex());
            outRef.recycle(outReader);
        } catch (CoverageStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        //create expression
        final FilterFactory2 ff = new ExtFilterFactory();
        final Expression exp;
        try {
            exp = CQL.parseExpression(inFormula, ff);
        } catch (CQLException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        // prepare dynamic pick object
        final GeneralDirectPosition positionGeo = new GeneralDirectPosition(gg.getCoordinateReferenceSystem());
        final DynamicPick pick;
        try {
            pick = new DynamicPick(inCoverages, inMapping, positionGeo);
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        final FillCoverage.SampleEvaluator evaluator = new FillCoverage.SampleEvaluator() {
            @Override
            public void evaluate(DirectPosition position, double[] sampleBuffer) {
                //update pick object position before evaluation
                positionGeo.setLocation(position);
                sampleBuffer[0] = exp.evaluate(pick, Double.class);
            }
        };
        
        final FillCoverage filler = new FillCoverage(outRef);
        try {
            filler.fill(evaluator, null);
        } catch (CoverageStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
    }
    
    //TODO, for later, handle offsets on axis with syntax U(x,y+10,z) and U(gx-20,gy,gz)
    private static class ExtFilterFactory extends WrapFilterFactory2{

        public ExtFilterFactory() {
            super((FilterFactory2)FactoryFinder.getFilterFactory(null));
        }

        @Override
        public Function function(String name, Expression... args) {
            return super.function(name, args);
        }
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
                baseToCoverage[i] = CRS.findMathTransform(coord.getCoordinateReferenceSystem(), coverages[i].getCoordinateReferenceSystem());
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
                        Logger.getLogger(MathCalcProcess.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
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
