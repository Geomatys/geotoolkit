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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
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

        if(bands.length!=4 || ranges.length!=4){
            throw new ProcessException("Bands and Ranges parameters must be of size 4.", this);
        }

        final SampleModel inputSampleModel = inputImage.getSampleModel();
        final int inputNbBand = inputSampleModel.getNumBands();
        final BufferedImage resultImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final WritableRaster raster = resultImage.getRaster();

        for (int i=0;i<bands.length;i++) {
            if (bands[i] > (inputNbBand-1)) {
                throw new ProcessException("Unvalid configuration, band "+bands[i]+" do not exist.", this);
            }
        }

        //copy datas
        final PixelIterator readIte = PixelIteratorFactory.createDefaultIterator(inputImage);
        final PixelIterator writeIte = PixelIteratorFactory.createDefaultWriteableIterator(raster, raster);
        final double[] pixel = new double[inputNbBand];
        final int[] rgba = new int[4];

        int srcBandIdx;
        int trgBandIdx;
        while (readIte.next() && writeIte.next()) {
            srcBandIdx = 0;
            trgBandIdx = 0;

            //read source pixels
            pixel[srcBandIdx] = readIte.getSampleDouble();
            while (++srcBandIdx != inputNbBand) {
                readIte.next();
                pixel[srcBandIdx] = readIte.getSampleDouble();
            }

            //calculate color
            boolean hasNan = false;
            for(int i=0;i<rgba.length;i++){
                if(bands[i]==-1){
                    //default value
                    rgba[i] = (i==3)?255:0;
                }else{
                    //calculate value
                    double v = pixel[bands[i]];
                    if(Double.isNaN(v)){
                        hasNan = true;
                    }
                    v = (pixel[bands[i]]-ranges[i][0]) / (ranges[i][1]-ranges[i][0]) * 255;
                    rgba[i] = XMath.clamp((int)v, 0, 255);
                }
            }

            if(hasNan){
                rgba[0]=0;rgba[1]=0;rgba[2]=0;rgba[3]=0;
            }

            //write target pixels
            writeIte.setSampleDouble(rgba[trgBandIdx]);
            while (++trgBandIdx != bands.length) {
                writeIte.next();
                writeIte.setSampleDouble(rgba[trgBandIdx]);
            }
        }

        outputParameters.getOrCreate(OUT_IMAGE).setValue(resultImage);
    }

}
