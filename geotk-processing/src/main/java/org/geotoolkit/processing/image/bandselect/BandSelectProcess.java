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
package org.geotoolkit.processing.image.bandselect;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.image.privy.ColorModelFactory;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;

import static org.geotoolkit.processing.image.bandselect.BandSelectDescriptor.*;

import org.geotoolkit.processing.image.reformat.ReformatProcess;
import org.opengis.parameter.ParameterValueGroup;

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

        final RenderedImage inputImage = inputParameters.getValue(IN_IMAGE);
        final int[] bands = inputParameters.getValue(IN_BANDS);

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
            raster = ReformatProcess.createRaster(inputType, width, height, nbBand, upperLeft);
        }catch(IllegalArgumentException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        //-- study Color Model
        final int dataType = inputImage.getSampleModel().getDataType();
        final SampleType st     = SampleType.valueOf(inputImage.getSampleModel().getDataType());
        //-- if we choose only one band grayScale else RGB
        final PhotometricInterpretation pI = (bands.length == 1) ? PhotometricInterpretation.GRAYSCALE : PhotometricInterpretation.RGB;
        ColorModel outCm = null;
        try {
            outCm = ImageUtils.createColorModel(st, bands.length, pI, false, false, null);
        } catch (Exception ex) {
            //various exceptions may happen here, RGB color model compatibility is obscur
            //fallback on grayscale
            outCm = ColorModelFactory.createGrayScale(dataType, nbBand, 0, 0, 1);
        }

        final BufferedImage resultImage    = new BufferedImage(outCm, raster, false, new Hashtable<>());

        //copy datas
        final PixelIterator readIte  = new PixelIterator.Builder().create(inputImage);
        final WritablePixelIterator writeIte = new PixelIterator.Builder().createWritable(raster);
        final double[] pixel = new double[inputNbBand];

        int trgBandIdx = 0;
        while (readIte.next() && writeIte.next()) {
            trgBandIdx = 0;

            //read source pixels
            readIte.getPixel(pixel);

            //write target pixels
            for (int b=0;b<bands.length;b++) {
                int tidx = bands[b];
                if (tidx != -1) writeIte.setSample(b, pixel[tidx]);
            }
        }

        outputParameters.getOrCreate(OUT_IMAGE).setValue(resultImage);
    }

}
