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
package org.geotoolkit.processing.coverage.bandselect;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.j2d.ColorModelFactory;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectProcess extends AbstractProcess {

    public BandSelectProcess(ParameterValueGroup input) {
        super(BandSelectDescriptor.INSTANCE, input);
    }

    /**
     *
     * @param coverage Coverage to process
     * @param bands bands to select for output
     */
    public BandSelectProcess(GridCoverage coverage, int[] bands){
        super(BandSelectDescriptor.INSTANCE, asParameters(coverage,bands));
    }

    private static ParameterValueGroup asParameters(GridCoverage coverage, int[] bands){
        final Parameters params = Parameters.castOrWrap(BandSelectDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(BandSelectDescriptor.IN_COVERAGE).setValue(coverage);
        params.getOrCreate(BandSelectDescriptor.IN_BANDS).setValue(bands);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return result coverage
     * @throws ProcessException
     */
    public GridCoverage executeNow() throws ProcessException {
        execute();
        return (GridCoverage) outputParameters.parameter(BandSelectDescriptor.OUT_COVERAGE.getName().getCode()).getValue();
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);
        final GridCoverage inputCoverage = (GridCoverage)inputParameters.getValue(BandSelectDescriptor.IN_COVERAGE);
        final int[] bands = inputParameters.getValue(BandSelectDescriptor.IN_BANDS);


        // CALL IMAGE BAND SELECT //////////////////////////////////////////////
        final ProcessDescriptor imageSelectDesc = org.geotoolkit.processing.image.bandselect.BandSelectDescriptor.INSTANCE;
        final Parameters params = Parameters.castOrWrap(imageSelectDesc.getInputDescriptor().createValue());
        params.parameter("image").setValue(inputCoverage.render(null));
        params.parameter("bands").setValue(bands);
        final Process process = imageSelectDesc.createProcess(params);
        BufferedImage resultImage = (BufferedImage)process.call().parameter("result").getValue();


        // BUILD A BETTER COLOR MODEL //////////////////////////////////////////
        //TODO try to reuse java colormodel if possible
        //extract grayscale min/max from sample dimension
        final SampleDimension gridSample = inputCoverage.getSampleDimensions().get(0);
        final ColorModel graycm = ColorModelFactory.createGrayScale(
                resultImage.getSampleModel().getDataType(),
                resultImage.getSampleModel().getNumBands(),0,
                SampleDimensionUtils.getMinimumValue(gridSample),
                SampleDimensionUtils.getMaximumValue(gridSample));
        resultImage = new BufferedImage(graycm, resultImage.getRaster(), false, new Hashtable<Object, Object>());


        // REBUILD COVERAGE ////////////////////////////////////////////////////
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(resultImage);
        gcb.setDomain(inputCoverage.getGridGeometry());
        final GridCoverage resultCoverage = gcb.build();

        outputParameters.getOrCreate(BandSelectDescriptor.OUT_COVERAGE).setValue(resultCoverage);
    }
}
