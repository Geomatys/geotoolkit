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
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.BufferedImages;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectProcess extends AbstractProcess {

    public BandSelectProcess(ParameterValueGroup input) {
        super(BandSelectDescriptor.INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);
        final GridCoverage2D inputCoverage = (GridCoverage2D) Parameters.getOrCreate(BandSelectDescriptor.IN_COVERAGE, inputParameters).getValue();
        final int[] bands = (int[]) Parameters.getOrCreate(BandSelectDescriptor.IN_BANDS, inputParameters).getValue();


        // CALL IMAGE BAND SELECT //////////////////////////////////////////////
        final ProcessDescriptor imageSelectDesc = org.geotoolkit.processing.image.bandselect.BandSelectDescriptor.INSTANCE;
        final ParameterValueGroup params = imageSelectDesc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputCoverage.getRenderedImage());
        params.parameter("bands").setValue(bands);
        final Process process = imageSelectDesc.createProcess(params);
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


        Parameters.getOrCreate(BandSelectDescriptor.OUT_COVERAGE, outputParameters).setValue(resultCoverage);
    }

}
