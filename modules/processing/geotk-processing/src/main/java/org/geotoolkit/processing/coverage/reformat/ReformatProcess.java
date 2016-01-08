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
package org.geotoolkit.processing.coverage.reformat;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Hashtable;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.processing.coverage.reformat.ReformatDescriptor.*;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.coverage.Coverage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReformatProcess extends AbstractProcess {

    public ReformatProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *
     * @param coverage coverage to process
     * @param dataType new output data type
     */
    public ReformatProcess(Coverage coverage, Integer dataType){
        super(ReformatDescriptor.INSTANCE, asParameters(coverage,dataType));
    }

    private static ParameterValueGroup asParameters(Coverage coverage, Integer dataType){
        final ParameterValueGroup params = ReformatDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, ReformatDescriptor.IN_COVERAGE.getName().getCode()).setValue(coverage);
        ParametersExt.getOrCreateValue(params, ReformatDescriptor.IN_DATATYPE.getName().getCode()).setValue(dataType);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return reformatted coverage
     * @throws ProcessException
     */
    public Coverage executeNow() throws ProcessException {
        execute();
        return (Coverage) outputParameters.parameter(ReformatDescriptor.OUT_COVERAGE.getName().getCode()).getValue();
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final GridCoverage2D inputCoverage = (GridCoverage2D) Parameters.getOrCreate(IN_COVERAGE, inputParameters).getValue();
        final int inputType = (Integer) Parameters.getOrCreate(IN_DATATYPE, inputParameters).getValue();
        final RenderedImage inputImage = inputCoverage.getRenderedImage();
        final SampleModel inputSampleModel = inputImage.getSampleModel();
        //check type, if same return the original coverage
        if(inputSampleModel.getDataType() == inputType){
            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(inputCoverage);
            return;
        }


        // CALL IMAGE BAND SELECT //////////////////////////////////////////////
        final ProcessDescriptor imageReformatDesc = org.geotoolkit.processing.image.reformat.ReformatDescriptor.INSTANCE;
        final ParameterValueGroup params = imageReformatDesc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputCoverage.getRenderedImage());
        params.parameter("datatype").setValue(inputType);
        final Process process = imageReformatDesc.createProcess(params);
        BufferedImage resultImage = (BufferedImage)process.call().parameter("result").getValue();


        // BUILD A BETTER COLOR MODEL //////////////////////////////////////////
        //TODO try to reuse java colormodel if possible
        //extract grayscale min/max from sample dimension
        final GridSampleDimension gridSample = inputCoverage.getSampleDimension(0);
        final ColorModel graycm = BufferedImages.createGrayScaleColorModel(
                resultImage.getSampleModel().getDataType(),
                resultImage.getSampleModel().getNumBands(),0,
                gridSample.getMinimumValue(), gridSample.getMaximumValue());
        resultImage = new BufferedImage(graycm, resultImage.getRaster(), false, new Hashtable<Object, Object>());


        // REBUILD COVERAGE ////////////////////////////////////////////////////
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(resultImage);
        gcb.setGridGeometry(inputCoverage.getGridGeometry());
        final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();


        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(resultCoverage);
    }

}
