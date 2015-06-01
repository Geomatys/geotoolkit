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
package org.geotoolkit.processing.coverage.bandcombine;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.util.Hashtable;

import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor.*;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.BufferedImages;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridGeometry;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class BandCombineProcess extends AbstractProcess {

    public BandCombineProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final Coverage[] inputCoverage = (Coverage[]) Parameters.getOrCreate(IN_COVERAGES, inputParameters).getValue();
        if(inputCoverage.length==0){
            throw new ProcessException("No coverage to combine", this, null);
        }else if(inputCoverage.length==1){
            //nothing to do
            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(inputCoverage[0]);
            return;
        }

        try {
            // CALL IMAGE BAND COMBINE /////////////////////////////////////////////
            final StringBuilder sb = new StringBuilder();
            final RenderedImage[] images = new RenderedImage[inputCoverage.length];

            for (int i = 0; i < inputCoverage.length; i++) {
                final GridCoverage2D gridCoverage2D = CoverageUtilities.firstSlice((GridCoverage) inputCoverage[i]);
                images[i] = gridCoverage2D.getRenderedImage();
                sb.append(String.valueOf(gridCoverage2D.getName()));
            }

            final ProcessDescriptor imageCombineDesc = org.geotoolkit.processing.image.bandcombine.BandCombineDescriptor.INSTANCE;
            final ParameterValueGroup params = imageCombineDesc.getInputDescriptor().createValue();
            params.parameter("images").setValue(images);
            final Process process = imageCombineDesc.createProcess(params);
            BufferedImage resultImage = (BufferedImage)process.call().parameter("result").getValue();


            // BUILD A BETTER COLOR MODEL //////////////////////////////////////////
            //TODO try to reuse java colormodel if possible
            //extract grayscale min/max from sample dimension
            final GridCoverage2D firstCoverage = CoverageUtilities.firstSlice((GridCoverage) inputCoverage[0]);
            final SampleDimension gridSample = firstCoverage.getSampleDimension(0);
            final GridGeometry gridGeometry = firstCoverage.getGridGeometry();
            final ColorModel graycm = BufferedImages.createGrayScaleColorModel(
                    resultImage.getSampleModel().getDataType(),
                    resultImage.getSampleModel().getNumBands(),0,
                    gridSample.getMinimumValue(), gridSample.getMaximumValue());
            resultImage = new BufferedImage(graycm, resultImage.getRaster(), false, new Hashtable<>());


            // REBUILD COVERAGE ////////////////////////////////////////////////////
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName(sb.toString());
            gcb.setRenderedImage(resultImage);
            gcb.setGridGeometry(gridGeometry);
            final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();


            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(resultCoverage);
        } catch (CoverageStoreException e) {
            throw new ProcessException(e.getMessage(),this, e);
        }
    }

}
