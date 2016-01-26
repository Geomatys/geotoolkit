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
package org.geotoolkit.processing.image.bandcombine;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineProcess extends AbstractProcess {

    public BandCombineProcess(ParameterValueGroup input) {
        super(BandCombineDescriptor.INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final RenderedImage[] inputImages = (RenderedImage[]) Parameters.getOrCreate(BandCombineDescriptor.IN_IMAGES, inputParameters).getValue();
        if(inputImages.length==0){
            throw new ProcessException("No image to combine", this, null);
        }else if(inputImages.length==1){
            //nothing to do
            Parameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE, outputParameters).setValue(inputImages[0]);
            return;
        }

        //check and extract informations, all images should have the same size and sample type.
        int sampleType = -1;
        int nbtotalbands = 0;
        int width = 0;
        int height = 0;
        Point upperLeft = null;
        final PixelIterator[] readItes = new PixelIterator[inputImages.length];
        final int[] nbBands = new int[inputImages.length];

        for(int i=0;i<inputImages.length;i++){
            final RenderedImage image = inputImages[i];
            final SampleModel sm = image.getSampleModel();
            if(sampleType==-1){
                //first image
                sampleType = sm.getDataType();
                width = image.getWidth();
                height = image.getHeight();
                upperLeft = new Point(image.getMinX(), image.getMinY());
            }else{
                //check same model
                if(sampleType != sm.getDataType()){
                    throw new ProcessException("Images do not have the same sample type", this, null);
                }
                if(width != image.getWidth() || height != image.getHeight()){
                    throw new ProcessException("Images do not have the same size", this, null);
                }
            }
            readItes[i] = PixelIteratorFactory.createDefaultIterator(image);
            nbBands[i] = sm.getNumBands();
            nbtotalbands += sm.getNumBands();
        }

        //try to reuse a java color model for better performances
        ColorModel cm = null;
        if(cm == null){
            //create a fallback grayscale colormodel which will always work
            cm = BufferedImages.createGrayScaleColorModel(sampleType,nbtotalbands,0,0,10);
        }

        final SampleModel sm = ImageUtils.createSampleModel(PlanarConfiguration.Interleaved, SampleType.valueOf(sampleType), width, height, nbtotalbands);

        final WritableRenderedImage resultImage = new WritableLargeRenderedImage(0, 0, width, height, new Dimension(256, 256), 0, 0, cm, sm);

        //copy datas
        final PixelIterator writeIte = PixelIteratorFactory.createDefaultWriteableIterator(resultImage, resultImage);
        final double[] pixel = new double[nbtotalbands];
        while(writeIte.next()){
            //read pixel from all input iterators
            int tband = -1;
            for(int i=0;i<readItes.length;i++){
                int sband = 0;
                readItes[i].next();
                pixel[++tband] = readItes[i].getSampleDouble();
                while (++sband != nbBands[i]) {
                    readItes[i].next();
                    pixel[++tband] = readItes[i].getSampleDouble();
                }
            }

            //write pixel
            tband = 0;
            writeIte.setSampleDouble(pixel[tband]);
            while (++tband != pixel.length) {
                writeIte.next();
                writeIte.setSampleDouble(pixel[tband]);
            }
        }

        Parameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE, outputParameters).setValue(resultImage);
    }

}
