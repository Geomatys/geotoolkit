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

public abstract class CoverageMathsWithValueAbstractProcess extends AbstractProcess {

    public CoverageMathsWithValueAbstractProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    protected CoverageMathsWithValueAbstractProcess(ProcessDescriptor desc) {
        super(desc);
    }

    protected GridCoverage executeOperation(GridCoverage inputCoverage, Double inputValue) throws ProcessException {

        final GridGeometry gg = inputCoverage.getGridGeometry();
        RenderedImage inputImage;
        GridGeometry outputGeom;

        if (gg.getDimension() <= 2) {
            inputImage = inputCoverage.render(null);
            outputGeom = inputCoverage.getGridGeometry();
        } else {
            final GridGeometryIterator sliceIt = new GridGeometryIterator(gg);
            if (!sliceIt.hasNext())
                throw new ProcessException("Input coverage is empty", this);
            final GridGeometry nextGeom = sliceIt.next();
            inputImage = inputCoverage.render(nextGeom.getExtent());
            outputGeom = nextGeom;
        }

        final SampleModel sm = inputImage.getSampleModel();
        int sampleType = sm.getDataType();
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        final BufferedImage img = BufferedImages.createImage(width, height, 1, sampleType);
        final WritablePixelIterator out = WritablePixelIterator.create(img);
        final org.apache.sis.image.PixelIterator ins = org.apache.sis.image.PixelIterator.create(inputImage);

        int y,x;
        for (y=0;y<height;y++) {
            for (x = 0; x < width; x++) {
                out.moveTo(x, y);
                ins.moveTo(x, y);
                double pixelValue = performOperation(ins.getSampleDouble(0), inputValue);
                out.setPixel(new double[]{pixelValue});
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(img);
        gcb.setDomain(outputGeom);
        gcb.setRanges(inputCoverage.getSampleDimensions());
        final GridCoverage resultCoverage = gcb.build();

        return resultCoverage;
    }

    protected abstract double performOperation(double value1, double value2);
}
