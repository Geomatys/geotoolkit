/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.image.replace;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.processing.image.replace.ReplaceDescriptor.*;

import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReplaceProcess extends AbstractProcess {
    
    private double[][][] replacements;
    
    public ReplaceProcess(BufferedImage image, double[][][] replacements){
        this(toParameters(image,replacements));
    }
    
    public ReplaceProcess(ParameterValueGroup input) {
        super(ReplaceDescriptor.INSTANCE, input);
    }

    private static ParameterValueGroup toParameters(BufferedImage image, double[][][] replacements){
        final ParameterValueGroup params = ReplaceDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, ReplaceDescriptor.IN_IMAGE.getName().getCode()).setValue(image);
        ParametersExt.getOrCreateValue(params, ReplaceDescriptor.IN_REPLACEMENTS.getName().getCode()).setValue(replacements);
        return params;
    }
    
    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final BufferedImage inputImage = (BufferedImage) Parameters.getOrCreate(IN_IMAGE, inputParameters).getValue();
        replacements = (double[][][]) Parameters.getOrCreate(IN_REPLACEMENTS, inputParameters).getValue();
        replacements = replacements.clone();
        
        //copy datas
        final int nbBand = inputImage.getSampleModel().getNumBands();
        final WritableRaster raster = inputImage.getRaster();
        final PixelIterator readIte = PixelIteratorFactory.createDefaultIterator(inputImage);
        final PixelIterator writeIte = PixelIteratorFactory.createDefaultWriteableIterator(raster, raster);

        int band;
        while (readIte.next() && writeIte.next()) {
            band = 0;
            writeIte.setSampleDouble(replace(readIte.getSampleDouble(),band));
            while (++band != nbBand) {
                readIte.next();
                writeIte.next();
                writeIte.setSampleDouble(replace(readIte.getSampleDouble(),band));
            }
        }
        
        Parameters.getOrCreate(OUT_IMAGE, outputParameters).setValue(inputImage);
    }
    
    private double replace(double value, int band){
        for(int i=0;i<replacements[band][0].length;i++){
            if(replacements[band][0][i]==value){
                return replacements[band][1][i];
            }
        }
        return value;
    }
    
}
