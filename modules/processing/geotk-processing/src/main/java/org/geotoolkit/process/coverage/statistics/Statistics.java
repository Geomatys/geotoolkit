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
package org.geotoolkit.process.coverage.statistics;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;
import static org.geotoolkit.process.coverage.statistics.StatisticsDescriptor.COVERAGE;
import static org.geotoolkit.process.coverage.statistics.StatisticsDescriptor.OUTCOVERAGE;

/**
 * Process to create a {@link org.geotoolkit.process.coverage.statistics.ImageStatistics} from a {@link org.geotoolkit.coverage.grid.GridCoverage2D}.
 * @author bgarcia
 */
public class Statistics extends AbstractProcess{

    public Statistics(GridCoverage2D coverage){
        this(toParameters(coverage));
    }

    public Statistics(final ParameterValueGroup input) {
        super(StatisticsDescriptor.INSTANCE, input);
    }

    private static ParameterValueGroup toParameters(GridCoverage2D inCoverages) {
        final ParameterValueGroup params = StatisticsDescriptor.INSTANCE.getInputDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, StatisticsDescriptor.COVERAGE.getName().getCode()).setValue(inCoverages);
        return params;
    }

    @Override
    protected void execute() throws ProcessException {
        GridCoverage2D coverage = value(COVERAGE, inputParameters);
        RenderedImage renderedImage = coverage.getRenderedImage();
        final GridSampleDimension[] sampleDimensions = coverage.getSampleDimensions();

        final SampleModel sm = renderedImage.getSampleModel();
        final int nbBands = sm.getNumBands();

        //create empty statistic object
        ImageStatistics sc = new ImageStatistics(nbBands);
        ImageStatistics.Band[] bands = sc.getBands();

        // this int permit to loop on images band.
        int b = 0;

        final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(renderedImage);

        //iter on each pixel band by band to add values on each band.
        while (pix.next()) {
            final double d = pix.getSampleDouble();
            bands[b].addValue(d);

            //reset b to loop on first band
            if (++b == nbBands) b = 0;
        }

        //add no data values on band
        for (int i = 0; i < sampleDimensions.length; i++) {
            bands[i].setNoData(sampleDimensions[i].getNoDataValues());
        }

        //return ImageStatistics
        getOrCreate(OUTCOVERAGE, outputParameters).setValue(sc);
    }
}
