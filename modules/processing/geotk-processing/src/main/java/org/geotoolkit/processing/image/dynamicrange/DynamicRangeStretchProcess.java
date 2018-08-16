/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.processing.image.dynamicrange;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Arrays;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.math.XMath;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.processing.image.dynamicrange.DynamicRangeStretchDescriptor.*;

import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DynamicRangeStretchProcess extends AbstractProcess {

    public DynamicRangeStretchProcess(RenderedImage input, int[] bands, double[][] ranges) {
        super(INSTANCE, asParameters(input,bands,ranges));
    }

    private static ParameterValueGroup asParameters(RenderedImage input, int[] bands, double[][] ranges){
        final Parameters params = Parameters.castOrWrap(DynamicRangeStretchDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(IN_IMAGE).setValue(input);
        params.getOrCreate(IN_BANDS).setValue(bands);
        params.getOrCreate(IN_RANGES).setValue(ranges);
        return params;
    }

    public DynamicRangeStretchProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    public BufferedImage executeNow() throws ProcessException {
        execute();
        return (BufferedImage) outputParameters.parameter(OUT_IMAGE.getName().getCode()).getValue();
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final RenderedImage inputImage = inputParameters.getValue(IN_IMAGE);
        final int[] bands              = inputParameters.getValue(IN_BANDS);
        final double[][] ranges        = inputParameters.getValue(IN_RANGES);

        if(bands.length < 3 || ranges.length < 3){
            throw new ProcessException("Bands and Ranges parameters must contain at least 3 components (red, green, blue).", this);
        }

        final SampleModel inputSampleModel = inputImage.getSampleModel();
        final int inputNbBand = inputSampleModel.getNumBands();

        for (int i=0;i<bands.length;i++) {
            if (bands[i] > (inputNbBand-1)) {
                throw new ProcessException("Invalid configuration, band "+bands[i]+" do not exist.", this);
            }
        }

        final boolean noAlpha = bands.length < 4;
        final BufferedImage resultImage = new BufferedImage(
                inputImage.getWidth(), inputImage.getHeight(),
                noAlpha? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
        );

        //copy data
        final PixelIterator readIte = PixelIterator.create(inputImage);
        final WritablePixelIterator writeIte = WritablePixelIterator.create(resultImage);
        final double[] pixel = new double[inputNbBand];
        final int[] rgba = new int[noAlpha ? 3 : 4];

        final double[] rangeRatios = new double[ranges.length];
        for (int i = 0; i < bands.length; i++) {
            if (bands[i] < 0) {
                continue;
            } else if (ranges.length <= i) {
                throw new ProcessException(String.format("%d bands defined, but only %d ranges", i, ranges.length) + i, this);
            } else if (ranges[i].length < 2) {
                throw new ProcessException("Bad definition for value range " + i, this);
            }
            final double span = ranges[i][1] - ranges[i][0];
            if (span > 0) {
                rangeRatios[i] = 255 / span;
            } else {
                rangeRatios[i] = 0;
            }
        }

        Point position;
        while (readIte.next()) {
            readIte.getPixel(pixel);
            position = readIte.getPosition();
            writeIte.moveTo(position.x, position.y);

            //calculate color
            boolean hasNan = false;
            for (int i = 0; i < rgba.length; i++) {
                if (bands[i] < 0) {
                    //default value
                    rgba[i] = (i == 3) ? 255 : 0;
                } else {
                    //calculate value
                    double v = pixel[bands[i]];
                    if (Double.isNaN(v)) {
                        hasNan = true;
                        break;
                    }
                    v = (v - ranges[i][0]) * rangeRatios[i];
                    rgba[i] = XMath.clamp((int) v, 0, 255);
                }
            }

            if (hasNan) {
                Arrays.fill(rgba, 0);
            }

            //write target pixels
            writeIte.setPixel(rgba);
        }

        outputParameters.getOrCreate(OUT_IMAGE).setValue(resultImage);
    }

}
