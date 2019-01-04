/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.parameter.Parameters;

import org.opengis.coverage.Coverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.storage.coverage.CoverageUtilities;

import static org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor.*;

/**
 * Combine each first slice of each {@link Coverage} given in parameters into one
 * other, where each bands from each internal source {@link RenderedImage} are merged into single image.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class BandCombineProcess extends AbstractProcess {

    public BandCombineProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *
     * @param coverages Coverages to combine
     */
    public BandCombineProcess(Coverage ... coverages){
        super(INSTANCE, asParameters(coverages));
    }

    private static ParameterValueGroup asParameters(Coverage ... coverages){
        final Parameters params = Parameters.castOrWrap(BandCombineDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(IN_COVERAGES).setValue(coverages);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return result coverage
     * @throws ProcessException
     */
    public GridCoverage2D executeNow() throws ProcessException {
        execute();
        return (GridCoverage2D)outputParameters.getValue(OUT_COVERAGE);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final Coverage[] inputCoverage = inputParameters.getValue(IN_COVERAGES);
        if (inputCoverage.length == 0) {
            throw new ProcessException("No coverage to combine", this, null);
        } else if (inputCoverage.length == 1) {
            //nothing to do
            outputParameters.getOrCreate(OUT_COVERAGE).setValue(inputCoverage[0]);
            return;
        }

        try {
            // CALL IMAGE BAND COMBINE /////////////////////////////////////////////
            final StringBuilder sb = new StringBuilder();
            final RenderedImage[] images = new RenderedImage[inputCoverage.length];
            final List<GridSampleDimension> sds = new ArrayList<>();

            for (int i = 0; i < inputCoverage.length; i++) {
                final GridCoverage2D gridCoverage2D = CoverageUtilities.firstSlice((GridCoverage) inputCoverage[i]);

                final GridSampleDimension[] gsd = gridCoverage2D.getSampleDimensions();
                if (gsd != null) sds.addAll(Arrays.asList(gsd));

                images[i] = gridCoverage2D.getRenderedImage();
                sb.append(String.valueOf(gridCoverage2D.getName()));
            }

            final ProcessDescriptor imageCombineDesc = org.geotoolkit.processing.image.bandcombine.BandCombineDescriptor.INSTANCE;
            final Parameters params = Parameters.castOrWrap(imageCombineDesc.getInputDescriptor().createValue());
            params.parameter("images").setValue(images);
            final Process process = imageCombineDesc.createProcess(params);
            RenderedImage resultImage = (RenderedImage)process.call().parameter("result").getValue();

            final GridCoverage2D firstCoverage = CoverageUtilities.firstSlice((GridCoverage) inputCoverage[0]);
            final GridGeometry gridGeometry    = firstCoverage.getGridGeometry();

            // REBUILD COVERAGE ////////////////////////////////////////////////////
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName(sb.toString());
            gcb.setRenderedImage(resultImage);
            gcb.setGridGeometry(gridGeometry);
            gcb.setSampleDimensions(sds.toArray(new GridSampleDimension[sds.size()]));
            final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();

            outputParameters.getOrCreate(OUT_COVERAGE).setValue(resultCoverage);
        } catch (CoverageStoreException e) {
            throw new ProcessException(e.getMessage(),this, e);
        }
    }
}
