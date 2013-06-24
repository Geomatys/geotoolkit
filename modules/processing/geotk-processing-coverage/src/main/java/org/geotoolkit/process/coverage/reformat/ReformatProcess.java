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
package org.geotoolkit.process.coverage.reformat;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferShort;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import javax.media.jai.RasterFactory;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.reformat.ReformatDescriptor.*;
import org.geotoolkit.util.ArgumentChecks;

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

        final GridCoverage2D inputCoverage = (GridCoverage2D) Parameters.getOrCreate(IN_COVERAGE, inputParameters).getValue();
        final int inputType = (Integer) Parameters.getOrCreate(IN_DATATYPE, inputParameters).getValue();
        final RenderedImage inputImage = inputCoverage.getRenderedImage();
        final SampleModel inputSampleModel = inputImage.getSampleModel();
        
        //check type, if same return the original coverage
        if(inputSampleModel.getDataType() == inputType){
            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(inputCoverage);
            return;
        }
        
        //create the output image
        final int width = inputImage.getWidth();
        final int height = inputImage.getHeight();
        final int nbBand = inputSampleModel.getNumBands();
        final Point upperLeft = new Point(inputImage.getMinX(), inputImage.getMinY());
        final WritableRaster raster;
        if(inputSampleModel.getNumBands() == 1){
            if(inputType == DataBuffer.TYPE_BYTE || inputType == DataBuffer.TYPE_USHORT || inputType == DataBuffer.TYPE_INT){
                raster = WritableRaster.createBandedRaster(inputType, width, height, nbBand, upperLeft);
            }else{
                //create it ourself
                final DataBuffer buffer;
                if(inputType == DataBuffer.TYPE_SHORT) buffer = new DataBufferShort(width*height);
                else if(inputType == DataBuffer.TYPE_FLOAT) buffer = new DataBufferFloat(width*height);
                else if(inputType == DataBuffer.TYPE_DOUBLE) buffer = new DataBufferDouble(width*height);
                else throw new ProcessException("Type not supported "+inputType, this, null);
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
                else throw new ProcessException("Type not supported "+inputType, this, null);
                final int[] bankIndices = new int[nbBand];
                final int[] bandOffsets = new int[nbBand];
                for(int i=1;i<nbBand;i++){
                    bandOffsets[i] = bandOffsets[i-1] + width*height;
                }
                //TODO create our own raster factory to avoid JAI
                raster = RasterFactory.createBandedRaster(buffer, width, height, width, bankIndices, bandOffsets, upperLeft);
            }
        }
        
        //TODO try to reuse java colormodel if possible
        //create a temporary fallback colormodel which will always work
        final int nbbitsPerSample = DataBuffer.getDataTypeSize(inputType);
        final GridSampleDimension gridSample = inputCoverage.getSampleDimension(0);
        //extract grayscale min/max from sample dimension
        final ColorModel graycm = new GrayScaleColorModel(nbbitsPerSample, 
                gridSample.getMinimumValue(), gridSample.getMaximumValue());
        
        
        final BufferedImage resultImage = new BufferedImage(graycm, raster, false, new Hashtable<Object, Object>());
        
        //copy datas
        final PixelIterator readIte = PixelIteratorFactory.createDefaultIterator(inputImage);
        final PixelIterator writeIte = PixelIteratorFactory.createDefaultWriteableIterator(raster, raster);
        while(readIte.next() && writeIte.next()){
//            System.out.println(readIte.getX()+" "+readIte.getY());
            for(int i=0;i<nbBand;i++){
                writeIte.setSampleDouble(readIte.getSampleDouble());
            }
        }
        
        //rebuild coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(resultImage);
        gcb.setGridGeometry(inputCoverage.getGridGeometry());
        final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();
        
        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(resultCoverage);
    }
    
}
