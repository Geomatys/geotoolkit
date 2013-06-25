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
package org.geotoolkit.process.coverage.combine;

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
import static org.geotoolkit.process.coverage.combine.BandCombineDescriptor.*;
import org.geotoolkit.process.coverage.reformat.GrayScaleColorModel;
import static org.geotoolkit.process.coverage.reformat.ReformatProcess.createRaster;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridGeometry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineProcess extends AbstractProcess {

    public BandCombineProcess(ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final Coverage[] inputCoverage = (Coverage[]) Parameters.getOrCreate(IN_COVERAGES, inputParameters).getValue();
        if(inputCoverage.length==0){
            throw new ProcessException("No coverage to combine", this, null);
        }else if(inputCoverage.length==1){
            //nothing to do
            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(inputCoverage[0]);
            return;
        }
        
        //check and extract informations, all coverages should have the same size and sample type.
        int sampleType = -1;
        int nbtotalbands = 0;
        int width = 0;
        int height = 0;
        GridSampleDimension gridSample = null;
        GridGeometry gridGeometry = null;
        Point upperLeft = null;
        final PixelIterator[] readItes = new PixelIterator[inputCoverage.length];
        final int[] nbBands = new int[inputCoverage.length];
        
        for(int i=0;i<inputCoverage.length;i++){
            final GridCoverage2D coverage = (GridCoverage2D) inputCoverage[i];
            final RenderedImage image = coverage.getRenderedImage();
            final SampleModel sm = image.getSampleModel();
            if(sampleType==-1){
                //first coverage
                sampleType = sm.getDataType();
                width = sm.getWidth();
                height = sm.getHeight();
                gridSample = coverage.getSampleDimension(0);
                upperLeft = new Point(image.getMinX(), image.getMinY());
                gridGeometry = coverage.getGridGeometry();
            }else{
                //check same model
                if(sampleType != sm.getDataType()){
                    throw new ProcessException("Coverages do not have the same sample type", this, null);
                }
                if(width != sm.getWidth() || height != sm.getHeight()){
                    throw new ProcessException("Coverages do not have the same size", this, null);
                }
            }
            readItes[i] = PixelIteratorFactory.createDefaultIterator(image);
            nbBands[i] = sm.getNumBands();
            nbtotalbands += sm.getNumBands();
        }
        
        final WritableRaster raster;
        try{
            raster = createRaster(sampleType, width, height, nbtotalbands, upperLeft);
        }catch(IllegalArgumentException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        //TODO try to reuse java colormodel if possible
        //create a temporary fallback colormodel which will always work
        final int nbbitsPerSample = DataBuffer.getDataTypeSize(sampleType);
        //extract grayscale min/max from sample dimension
        final ColorModel graycm = new GrayScaleColorModel(nbbitsPerSample, 
                gridSample.getMinimumValue(), gridSample.getMaximumValue());
        
        final BufferedImage resultImage = new BufferedImage(graycm, raster, false, new Hashtable<Object, Object>());
        
        //copy datas        
        final PixelIterator writeIte = PixelIteratorFactory.createDefaultWriteableIterator(raster, raster);
        while(writeIte.next()){
            for(int i=0;i<readItes.length;i++){
                readItes[i].next();
                for(int b=0;b<nbBands[i];b++){
                    writeIte.setSampleDouble(readItes[i].getSampleDouble());
                }
            }
        }
        
        //rebuild coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(resultImage);
        gcb.setGridGeometry(gridGeometry);
        final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();
        
        
        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(resultCoverage);
    }
    
}
