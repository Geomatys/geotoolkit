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

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.cql.CQLException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.util.Utilities;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.filter.Expression;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.multires.TiledResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcProcess extends AbstractProcess {

    public MathCalcProcess(GridCoverage[] inCoverages, String inFormula, String[] inMapping, GridCoverageResource outCoverage){
        this(toParameters(inCoverages, inFormula, inMapping, outCoverage));
    }

    public MathCalcProcess(ParameterValueGroup params) {
        super(MathCalcDescriptor.INSTANCE, params);
    }

    private static ParameterValueGroup toParameters(GridCoverage[] inCoverages, String inFormula, String[] inMapping, GridCoverageResource outCoverage){
        final Parameters params = Parameters.castOrWrap(MathCalcDescriptor.INSTANCE.getInputDescriptor().createValue());
        params.getOrCreate(MathCalcDescriptor.IN_COVERAGES).setValue(inCoverages);
        params.getOrCreate(MathCalcDescriptor.IN_FORMULA).setValue(inFormula);
        params.getOrCreate(MathCalcDescriptor.IN_MAPPING).setValue(inMapping);
        params.getOrCreate(MathCalcDescriptor.IN_RESULT_COVERAGE).setValue(outCoverage);
        return params;
    }

    @Override
    protected void execute() throws ProcessException {
        final GridCoverage[] inCoverages = inputParameters.getValue(MathCalcDescriptor.IN_COVERAGES);
        final String inFormula = inputParameters.getValue(MathCalcDescriptor.IN_FORMULA);
        final String[] inMapping = inputParameters.getValue(MathCalcDescriptor.IN_MAPPING);
        final WritableGridCoverageResource outRef = inputParameters.getValue(MathCalcDescriptor.IN_RESULT_COVERAGE);

        final GridGeometry gg;
        try {
            gg = outRef.getGridGeometry();
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        //create expression
        final FilterFactory2 ff = FilterUtilities.FF;
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
            if(outRef instanceof TiledResource){
                filler.fill((TiledResource)outRef, evaluator);
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

    /**
     * Find common crs which can be used for mathcalc process.
     */
    public static CoordinateReferenceSystem findCommunCrs(CoordinateReferenceSystem ... crss) throws IllegalArgumentException{
        CoordinateReferenceSystem result = null;
        for(CoordinateReferenceSystem crs : crss){
            if (result == null) {
                result = crs;
            } else {
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
