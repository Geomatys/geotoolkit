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
package org.geotoolkit.process.coverage.bandselect;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.bandselect.BandSelectDescriptor.*;
import org.geotoolkit.process.coverage.reformat.GrayScaleColorModel;
import static org.geotoolkit.process.coverage.reformat.ReformatProcess.createRaster;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectProcess extends AbstractProcess {

    public BandSelectProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final GridCoverage2D inputCoverage = (GridCoverage2D) Parameters.getOrCreate(IN_COVERAGE, inputParameters).getValue();
        final int[] bands = (int[]) Parameters.getOrCreate(IN_BANDS, inputParameters).getValue();
        
        final RenderedImage inputImage = inputCoverage.getRenderedImage();
        final SampleModel inputSampleModel = inputImage.getSampleModel();
        final int inputNbBand = inputSampleModel.getNumBands();
        final int inputType = inputSampleModel.getDataType();
        
        //check band indexes
        for(int targetIndex=0;targetIndex<bands.length;targetIndex++){
            if(bands[targetIndex] >= inputNbBand){
                //wrong index, no band for this index
                throw new ProcessException("Invalid band index "+bands[targetIndex]+" , image only have "+inputNbBand+" bands", this, null);
            }
        }
        
        //create the output image
        final int width = inputImage.getWidth();
        final int height = inputImage.getHeight();
        final int nbBand = bands.length;
        final Point upperLeft = new Point(inputImage.getMinX(), inputImage.getMinY());
        final WritableRaster raster;
        try{
            raster = createRaster(inputType, width, height, nbBand, upperLeft);
        }catch(IllegalArgumentException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
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
        final double[] pixel = new double[inputNbBand];
        
        int srcBandIdx = 0;
        int trgBandIdx = 0;
        while (readIte.next() && writeIte.next()) {
            srcBandIdx = 0;
            trgBandIdx = 0;
            
            //read source pixels
            pixel[srcBandIdx] = readIte.getSampleDouble();
            while (++srcBandIdx != inputNbBand) {
                readIte.next();
                pixel[srcBandIdx] = readIte.getSampleDouble();
            }
            
            //write target pixels
            writeIte.setSampleDouble(pixel[bands[trgBandIdx]]);
            while (++trgBandIdx != bands.length) {
                writeIte.next();
                writeIte.setSampleDouble(pixel[bands[trgBandIdx]]);
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
