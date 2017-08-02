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

import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.WrapFilterFactory2;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.coverage.Coverage;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcProcess extends AbstractProcess {

    public MathCalcProcess(Coverage[] inCoverages, String inFormula, String[] inMapping, CoverageResource outCoverage){
        this(toParameters(inCoverages, inFormula, inMapping, outCoverage));
    }

    public MathCalcProcess(ParameterValueGroup params) {
        super(MathCalcDescriptor.INSTANCE, params);
    }

    private static ParameterValueGroup toParameters(Coverage[] inCoverages, String inFormula, String[] inMapping, CoverageResource outCoverage){
        final Parameters params = Parameters.castOrWrap(MathCalcDescriptor.INSTANCE.getInputDescriptor().createValue());
        params.getOrCreate(MathCalcDescriptor.IN_COVERAGES).setValue(inCoverages);
        params.getOrCreate(MathCalcDescriptor.IN_FORMULA).setValue(inFormula);
        params.getOrCreate(MathCalcDescriptor.IN_MAPPING).setValue(inMapping);
        params.getOrCreate(MathCalcDescriptor.IN_RESULT_COVERAGE).setValue(outCoverage);
        return params;
    }

    @Override
    protected void execute() throws ProcessException {
        final Coverage[] inCoverages = inputParameters.getValue(MathCalcDescriptor.IN_COVERAGES);
        final String inFormula = inputParameters.getValue(MathCalcDescriptor.IN_FORMULA);
        final String[] inMapping = inputParameters.getValue(MathCalcDescriptor.IN_MAPPING);
        final CoverageResource outRef = inputParameters.getValue(MathCalcDescriptor.IN_RESULT_COVERAGE);

        final GeneralGridGeometry gg;
        final GridCoverageReader outReader;
        try {
            outReader = outRef.acquireReader();
            gg = outReader.getGridGeometry(outRef.getImageIndex());
            outRef.recycle(outReader);
        } catch (DataStoreException ex) {
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
        final MathCalcCoverageEvaluator evaluator;
        try {
            evaluator = new MathCalcCoverageEvaluator(inCoverages,inMapping,exp,gg.getCoordinateReferenceSystem());
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        final FillCoverage filler = new FillCoverage();
        try {
            if(outRef instanceof PyramidalCoverageResource){
                filler.fill((PyramidalCoverageResource)outRef, evaluator);
            }else{
                filler.fill(outRef, evaluator, null);
            }
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (FactoryException ex) {
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


    /**
     * Find common crs which can be used for mathcalc process.
     *
     * @param crss
     * @return
     * @throws IllegalArgumentException
     */
    public static CoordinateReferenceSystem findCommunCrs(CoordinateReferenceSystem ... crss) throws IllegalArgumentException{

        CoordinateReferenceSystem result = null;

        for(CoordinateReferenceSystem crs : crss){
            if(result==null){
                result = crs;
            }else{
                final int nbr = result.getCoordinateSystem().getDimension();
                final int nbc = crs.getCoordinateSystem().getDimension();

                if (nbr==nbc && Utilities.equalsIgnoreMetadata(nbr, nbc)) {
                    //same number of dimensions and equal, OK
                }else{
                    throw new IllegalArgumentException("CRS have different number of dimensions");
                }
            }
        }

        return result;
    }


}
