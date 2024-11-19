/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.processing.coverage.math;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.WritablePixelIterator;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

public abstract class CoverageMathsAbstractProcess extends AbstractProcess {

    public CoverageMathsAbstractProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    protected CoverageMathsAbstractProcess(ProcessDescriptor desc) {
        super(desc);
    }

    protected GridCoverage executeOperation(GridCoverage firstCoverage, GridCoverage secondCoverage) throws ProcessException {
        final GridGeometry outputGeom;
        final RenderedImage firstImage, secondImage;

        final GridGeometry firstGg = firstCoverage.getGridGeometry();
        final GridGeometry secondGg = secondCoverage.getGridGeometry();

        if (firstGg.getDimension() <= 2) {
            firstImage = firstCoverage.render(null);
        } else {
            final GridGeometryIterator sliceIt = new GridGeometryIterator(firstGg);
            if (!sliceIt.hasNext())
                throw new ProcessException("First input coverage is empty", this);
            final GridGeometry nextGeom = sliceIt.next();
            firstImage = firstCoverage.render(nextGeom.getExtent());
        }

        if (secondGg.getDimension() <= 2) {
            secondImage = secondCoverage.render(null);
            outputGeom = secondCoverage.getGridGeometry();
        } else {
            final GridGeometryIterator sliceIt = new GridGeometryIterator(secondGg);
            if (!sliceIt.hasNext())
                throw new ProcessException("Second input coverage is empty", this);
            final GridGeometry nextGeom = sliceIt.next();
            outputGeom = nextGeom;
            secondImage = secondCoverage.render(nextGeom.getExtent());
        }

        final SampleModel firstSm = firstImage.getSampleModel();
        final SampleModel secondSm = secondImage.getSampleModel();

        int sampleType = firstSm.getDataType();
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();

        if (sampleType != secondSm.getDataType())
            throw new ProcessException("Second Image do not have the same sample type", this, null);
        if (width != secondImage.getWidth() || height != secondImage.getHeight())
            throw new ProcessException("Second Image do not have the same size", this, null);

        final BufferedImage img = BufferedImages.createImage(width, height, 1, sampleType);

        final org.apache.sis.image.PixelIterator[] ins = new org.apache.sis.image.PixelIterator[2];
        ins[0] = org.apache.sis.image.PixelIterator.create(firstImage);
        ins[1] = org.apache.sis.image.PixelIterator.create(secondImage);

        final WritablePixelIterator out = WritablePixelIterator.create(img);

        int y, x;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                out.moveTo(x, y);
                ins[0].moveTo(x, y);
                ins[1].moveTo(x, y);
                double pixelValue = performOperation(ins[0].getSampleDouble(0), ins[1].getSampleDouble(0));
                out.setPixel(new double[]{pixelValue});
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(img);
        gcb.setDomain(outputGeom);
        gcb.setRanges(firstCoverage.getSampleDimensions());
        final GridCoverage resultCoverage = gcb.build();

        return resultCoverage;
    }

    protected abstract double performOperation(double value1, double value2);
}
