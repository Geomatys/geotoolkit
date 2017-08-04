/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.image.statistics;

import java.awt.image.RenderedImage;
import org.apache.sis.math.Statistics;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.processing.image.statistics.ImageStatisticsDescriptor.*;

/**
 * Process which fill and return a {@link Statistics} object for each {@link RenderedImage} bands.
 *
 * @author Remi Marechal (geomatys).
 */
public class ImageStatisticsProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public ImageStatisticsProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final RenderedImage inImg = inputParameters.getValue(INPUT_IMAGE);

        final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(inImg);

        final int numBand = pix.getNumBands();
        final Statistics[] stats = new Statistics[numBand];
        for (int b = 0; b < numBand; b++) {
            stats[b] = new Statistics("statistic from band "+b);
        }

        int b = 0;
        while (pix.next()) {
            stats[b].accept(pix.getSampleDouble());
            if (++b == numBand) b = 0;
        }

        outputParameters.getOrCreate(OUTPUT_STATS).setValue(stats);
    }
}
