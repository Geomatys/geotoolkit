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
import org.apache.sis.parameter.Parameters;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
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
        final Parameters params = Parameters.castOrWrap(ReplaceDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(ReplaceDescriptor.IN_IMAGE).setValue(image);
        params.getOrCreate(ReplaceDescriptor.IN_REPLACEMENTS).setValue(replacements);
        return params;
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final BufferedImage inputImage = inputParameters.getValue(IN_IMAGE);
        replacements = inputParameters.getValue(IN_REPLACEMENTS);
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

        outputParameters.getOrCreate(OUT_IMAGE).setValue(inputImage);
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
