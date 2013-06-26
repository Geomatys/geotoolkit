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
package org.geotoolkit.process.coverage.bandcombine;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.bandcombine.BandCombineDescriptor.*;
import org.geotoolkit.process.image.reformat.GrayScaleColorModel;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
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

        // PARAMETERS CHECK ////////////////////////////////////////////////////
        final Coverage[] inputCoverage = (Coverage[]) Parameters.getOrCreate(IN_COVERAGES, inputParameters).getValue();
        if(inputCoverage.length==0){
            throw new ProcessException("No coverage to combine", this, null);
        }else if(inputCoverage.length==1){
            //nothing to do
            Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(inputCoverage[0]);
            return;
        }
        
        
        // CALL IMAGE BAND COMBINE /////////////////////////////////////////////
        final RenderedImage[] images = new RenderedImage[inputCoverage.length];
        for(int i=0;i<inputCoverage.length;i++){
            images[i] = ((GridCoverage2D) inputCoverage[i]).getRenderedImage();
        }
        final ProcessDescriptor imageCombineDesc = org.geotoolkit.process.image.bandcombine.BandCombineDescriptor.INSTANCE;
        final ParameterValueGroup params = imageCombineDesc.getInputDescriptor().createValue();
        params.parameter("images").setValue(images);
        final Process process = imageCombineDesc.createProcess(params);
        BufferedImage resultImage = (BufferedImage)process.call().parameter("result").getValue();
        
        
        // BUILD A BETTER COLOR MODEL //////////////////////////////////////////
        //TODO try to reuse java colormodel if possible
        //extract grayscale min/max from sample dimension
        SampleDimension gridSample = inputCoverage[0].getSampleDimension(0);
        GridGeometry gridGeometry = ((GridCoverage2D)inputCoverage[0]).getGridGeometry();
        final ColorModel graycm = new GrayScaleColorModel(
                DataBuffer.getDataTypeSize(resultImage.getSampleModel().getDataType()), 
                gridSample.getMinimumValue(), gridSample.getMaximumValue());
        resultImage = new BufferedImage(graycm, resultImage.getRaster(), false, new Hashtable<Object, Object>());
        
        
        // REBUILD COVERAGE ////////////////////////////////////////////////////
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(resultImage);
        gcb.setGridGeometry(gridGeometry);
        final GridCoverage2D resultCoverage = gcb.getGridCoverage2D();
        
        
        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(resultCoverage);
    }
    
}
