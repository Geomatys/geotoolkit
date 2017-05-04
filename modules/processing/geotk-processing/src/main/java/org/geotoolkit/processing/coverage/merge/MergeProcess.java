/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.merge;

import java.awt.image.DataBuffer;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.processing.coverage.merge.MergeDescriptor.*;
import org.geotoolkit.processing.coverage.reformat.ReformatDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MergeProcess extends AbstractProcess {

    public MergeProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *
     * @param coverages coverage to merge
     * @param env area to merge
     */
    public MergeProcess(Coverage[] coverages, Envelope env){
        super(MergeDescriptor.INSTANCE, asParameters(coverages,env));
    }

    private static ParameterValueGroup asParameters(Coverage[] coverages, Envelope env){
        final ParameterValueGroup params = MergeDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, MergeDescriptor.IN_COVERAGES.getName().getCode()).setValue(coverages);
        if(env!=null)ParametersExt.getOrCreateValue(params, MergeDescriptor.IN_ENVELOPE.getName().getCode()).setValue(env);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return merged coverage
     * @throws ProcessException
     */
    public Coverage executeNow() throws ProcessException {
        execute();
        return (Coverage) outputParameters.parameter(MergeDescriptor.OUT_COVERAGE.getName().getCode()).getValue();
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final Coverage[] inputCoverage = (Coverage[]) Parameters.getOrCreate(IN_COVERAGES, inputParameters).getValue();
        final Envelope inputEnvelope = (Envelope) Parameters.getOrCreate(IN_ENVELOPE, inputParameters).getValue();
        final double inputResolution = (Double) Parameters.getOrCreate(IN_RESOLUTION, inputParameters).getValue();

        //find the best data type;
        int datatype = -1;
        for(Coverage gc : inputCoverage){
            final int gctype = ((GridCoverage2D)gc).getRenderedImage().getSampleModel().getDataType();
            if(datatype==-1){
                datatype = gctype;
            }else{
                //find the largest type
                datatype = largest(datatype, gctype);
            }
        }

        //calculate the output grid geometry and image size
        final int sizeX = (int)(inputEnvelope.getSpan(0) / inputResolution);
        final int sizeY = (int)(inputEnvelope.getSpan(1) / inputResolution);
        final GridGeometry2D gridGeom = new GridGeometry2D(
                new GridEnvelope2D(0, 0, sizeX, sizeY), inputEnvelope);

        //force sample type and area of each coverage
        final Coverage[] fittedCoverages = new Coverage[inputCoverage.length];
        for(int i=0;i<inputCoverage.length;i++){
            fittedCoverages[i] = inputCoverage[i];

            //Reformat
            final ProcessDescriptor coverageReformatDesc = ReformatDescriptor.INSTANCE;
            final ParameterValueGroup reformatParams = coverageReformatDesc.getInputDescriptor().createValue();
            reformatParams.parameter("coverage").setValue(fittedCoverages[i]);
            reformatParams.parameter("datatype").setValue(datatype);
            final Process reformatProcess = coverageReformatDesc.createProcess(reformatParams);
            fittedCoverages[i] = (GridCoverage2D)reformatProcess.call().parameter("result").getValue();

            //Resample
            final ProcessDescriptor coverageResampleDesc = ResampleDescriptor.INSTANCE;
            final ParameterValueGroup resampleParams = coverageResampleDesc.getInputDescriptor().createValue();
            resampleParams.parameter("Source").setValue(fittedCoverages[i]);
            resampleParams.parameter("GridGeometry").setValue(gridGeom);
            resampleParams.parameter("CoordinateReferenceSystem").setValue(inputEnvelope.getCoordinateReferenceSystem());
            final Process resampleProcess = coverageResampleDesc.createProcess(resampleParams);
            fittedCoverages[i] = (GridCoverage2D)resampleProcess.call().parameter("result").getValue();
        }

        //Band combine
        final ProcessDescriptor coverageResampleDesc = BandCombineDescriptor.INSTANCE;
        final ParameterValueGroup resampleParams = coverageResampleDesc.getInputDescriptor().createValue();
        resampleParams.parameter("coverages").setValue(fittedCoverages);
        final Process resampleProcess = coverageResampleDesc.createProcess(resampleParams);
        final Coverage result = (GridCoverage2D)resampleProcess.call().parameter("result").getValue();

        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(result);
    }

    private static int largest(int datatype1, int datatype2){
        if(datatype1 == DataBuffer.TYPE_DOUBLE || datatype2 == DataBuffer.TYPE_DOUBLE){
            return DataBuffer.TYPE_DOUBLE;
        }else if(datatype1 == DataBuffer.TYPE_FLOAT || datatype2 == DataBuffer.TYPE_FLOAT){
            return DataBuffer.TYPE_FLOAT;
        }else if(datatype1 == DataBuffer.TYPE_INT || datatype2 == DataBuffer.TYPE_INT){
            return DataBuffer.TYPE_INT;
        }else if(datatype1 == DataBuffer.TYPE_USHORT || datatype2 == DataBuffer.TYPE_USHORT){
            return DataBuffer.TYPE_USHORT;
        }else if(datatype1 == DataBuffer.TYPE_SHORT || datatype2 == DataBuffer.TYPE_SHORT){
            return DataBuffer.TYPE_USHORT;
        }else if(datatype1 == DataBuffer.TYPE_BYTE || datatype2 == DataBuffer.TYPE_BYTE){
            return DataBuffer.TYPE_BYTE;
        }
        return DataBuffer.TYPE_UNDEFINED;
    }

}
