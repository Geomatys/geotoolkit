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
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.WrapFilterFactory2;
import static org.geotoolkit.parameter.Parameters.value;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcProcess extends AbstractProcess {

    public MathCalcProcess(Coverage[] inCoverages, Expression inFormula, String[] inMapping, GridCoverage outCoverage){
        this(toParameters(inCoverages, inFormula, inMapping, outCoverage));
    }
    
    public MathCalcProcess(ParameterValueGroup params) {
        super(MathCalcDescriptor.INSTANCE, params);
    }

    private static ParameterValueGroup toParameters(Coverage[] inCoverages, Expression inFormula, String[] inMapping, GridCoverage outCoverage){
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
        final GridCoverage outCoverage = value(MathCalcDescriptor.IN_RESULT_COVERAGE, inputParameters);
        
        //create expression
        final FilterFactory2 ff = new ExtFilterFactory();
        final Expression exp;
        try {
            exp = CQL.parseExpression(inFormula, ff);
        } catch (CQLException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        
        //loop on all output voxel
        final GridGeometry gg = outCoverage.getGridGeometry();
        final GridEnvelope ge = gg.getExtent();
        final int nbDim = ge.getDimension();
        
        final int[] coord = new int[nbDim];
        final int[] mins = new int[nbDim];
        final int[] maxs = new int[nbDim];
        for(int i=0;i<nbDim;i++){
            mins[i] = ge.getLow(i);
            maxs[i] = ge.getHigh(i);
            coord[i] = mins[i];
        }
        
        for(;coord[nbDim-1]<maxs[nbDim-1];){
            
            //TODO
            
            
            
            //prepare next iteration
            for(int z=0;z<nbDim;z++){
                coord[z]++;
                if(coord[z]<maxs[z]) break;
                //iterate it next dim
                coord[z]=mins[z];
            }
        }
        
    }
    
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
        private final int[] coord;
        
        private DynamicPick(Coverage[] coverages, String[] mapping, int[] coord){
            this.coverages = coverages;
            this.mapping = mapping;
            this.coord = coord;
        }
        
        @Override
        public Object get(Object key) {
            //coverages[0].evaluate(null, null);
            return null;
        }

        @Override
        public Set entrySet() {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
}
