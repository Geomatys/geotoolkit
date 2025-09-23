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
package org.geotoolkit.processing.image.reformat;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import javax.media.jai.RasterFactory;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.image.internal.shared.ColorModelFactory;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.image.reformat.ReformatDescriptor.*;

import org.apache.sis.util.ArgumentChecks;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReformatProcess extends AbstractProcess {

    public ReformatProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final RenderedImage inputImage = inputParameters.getValue(IN_IMAGE);
        final int inputType = inputParameters.getValue(IN_DATATYPE);
        final SampleModel inputSampleModel = inputImage.getSampleModel();

        //check type, if same return the original coverage
        if(inputSampleModel.getDataType() == inputType){
            outputParameters.getOrCreate(OUT_IMAGE).setValue(inputImage);
            return;
        }

        //create the output image
        final int width = inputImage.getWidth();
        final int height = inputImage.getHeight();
        final int nbBand = inputSampleModel.getNumBands();
        final Point upperLeft = new Point(inputImage.getMinX(), inputImage.getMinY());
        final WritableRaster raster;
        try{
            raster = createRaster(inputType, width, height, nbBand, upperLeft);
        }catch(IllegalArgumentException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        //TODO try to reuse java colormodel if possible
        //create a temporary fallback colormodel which will always work
        //extract grayscale min/max from sample dimension
        final ColorModel graycm = ColorModelFactory.createGrayScale(inputType,nbBand,0,0,10);


        final BufferedImage resultImage = new BufferedImage(graycm, raster, false, new Hashtable<Object, Object>());

        //copy datas
        final PixelIterator readIte = new PixelIterator.Builder().create(inputImage);
        final WritablePixelIterator writeIte = new PixelIterator.Builder().createWritable(raster);

        while (readIte.next() && writeIte.next()) {
            for (int b = 0; b < nbBand; b++) {
                writeIte.setSample(b, readIte.getSampleDouble(b));
            }
        }

        outputParameters.getOrCreate(OUT_IMAGE).setValue(resultImage);
    }

    public static WritableRaster createRaster(int inputType, int width, int height, int nbBand, Point upperLeft) throws IllegalArgumentException{
        final WritableRaster raster;
        if(nbBand == 1){
            if(inputType == DataBuffer.TYPE_BYTE || inputType == DataBuffer.TYPE_USHORT || inputType == DataBuffer.TYPE_INT){
                raster = WritableRaster.createBandedRaster(inputType, width, height, nbBand, upperLeft);
            }else{
                //create it ourself
                final DataBuffer buffer;
                if(inputType == DataBuffer.TYPE_SHORT) buffer = new DataBufferShort(width*height);
                else if(inputType == DataBuffer.TYPE_FLOAT) buffer = new DataBufferFloat(width*height);
                else if(inputType == DataBuffer.TYPE_DOUBLE) buffer = new DataBufferDouble(width*height);
                else throw new IllegalArgumentException("Type not supported "+inputType);
                final int[] zero = new int[1];
                //TODO create our own raster factory to avoid JAI
                raster = RasterFactory.createBandedRaster(buffer, width, height, width, zero, zero, upperLeft);
            }

        }else{
            if(inputType == DataBuffer.TYPE_BYTE || inputType == DataBuffer.TYPE_USHORT){
                raster = WritableRaster.createInterleavedRaster(inputType, width, height, nbBand, upperLeft);
            }else{
                //create it ourself
                final DataBuffer buffer;
                if(inputType == DataBuffer.TYPE_SHORT) buffer = new DataBufferShort(width*height*nbBand);
                else if(inputType == DataBuffer.TYPE_FLOAT) buffer = new DataBufferFloat(width*height*nbBand);
                else if(inputType == DataBuffer.TYPE_DOUBLE) buffer = new DataBufferDouble(width*height*nbBand);
                else if(inputType == DataBuffer.TYPE_INT) buffer = new DataBufferInt(width*height*nbBand);
                else throw new IllegalArgumentException("Type not supported "+inputType);
                final int[] bankIndices = new int[nbBand];
                final int[] bandOffsets = new int[nbBand];
                for(int i=1;i<nbBand;i++){
                    bandOffsets[i] = bandOffsets[i-1] + width*height;
                }
                //TODO create our own raster factory to avoid JAI
                raster = RasterFactory.createBandedRaster(buffer, width, height, width, bankIndices, bandOffsets, upperLeft);
            }
        }
        return raster;
    }

}
