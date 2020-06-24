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
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Combine each first slice of each {@link GridCoverage} given in parameters into one
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
    public BandCombineProcess(GridCoverage ... coverages){
        super(INSTANCE, asParameters(coverages));
    }

    private static ParameterValueGroup asParameters(GridCoverage ... coverages){
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
    public GridCoverage executeNow() throws ProcessException {
        execute();
        return outputParameters.getValue(OUT_COVERAGE);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final GridCoverage[] inputCoverage = inputParameters.getValue(IN_COVERAGES);
        if (inputCoverage.length == 0) {
            throw new ProcessException("No coverage to combine", this, null);
        } else if (inputCoverage.length == 1) {
            //nothing to do
            outputParameters.getOrCreate(OUT_COVERAGE).setValue(inputCoverage[0]);
            return;
        }

        GridGeometry outputGeom = null; // TODO: better logic
        // CALL IMAGE BAND COMBINE /////////////////////////////////////////////
        final RenderedImage[] images = new RenderedImage[inputCoverage.length];
        final List<SampleDimension> sds = new ArrayList<>();

        for (int i = 0; i < inputCoverage.length; i++) {
            final GridCoverage gridCoverage2D = inputCoverage[i];

            final List<SampleDimension> covSds = gridCoverage2D.getSampleDimensions();
            if (covSds.isEmpty())
                throw new ProcessException("Cannot extract sample dimension from input coverage "+i, this);
            sds.addAll(covSds);


            final GridGeometry gg = gridCoverage2D.getGridGeometry();
            if (gg.getDimension() <= 2) {
                images[i] = gridCoverage2D.render(null);
                if (outputGeom == null) outputGeom = gridCoverage2D.getGridGeometry();
            } else {
                final GridGeometryIterator sliceIt = new GridGeometryIterator(gg);
                if (!sliceIt.hasNext())
                    throw new ProcessException("Input coverage [at index "+i+"] is empty", this);
                final GridGeometry nextGeom = sliceIt.next();
                if (outputGeom == null) outputGeom = nextGeom;
                images[i] = gridCoverage2D.render(nextGeom.getExtent());
            }
        }

        final ProcessDescriptor imageCombineDesc = org.geotoolkit.processing.image.bandcombine.BandCombineDescriptor.INSTANCE;
        final Parameters params = Parameters.castOrWrap(imageCombineDesc.getInputDescriptor().createValue());
        params.parameter("images").setValue(images);
        final Process process = imageCombineDesc.createProcess(params);
        RenderedImage resultImage = (RenderedImage)process.call().parameter("result").getValue();

        // REBUILD COVERAGE ////////////////////////////////////////////////////
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(resultImage);
        gcb.setDomain(outputGeom);
        gcb.setRanges(sds);
        final GridCoverage resultCoverage = gcb.build();

        outputParameters.getOrCreate(OUT_COVERAGE).setValue(resultCoverage);
    }
}
