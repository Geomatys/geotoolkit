/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2014, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2014, Geomatys
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
package org.geotoolkit.image.io.plugin.TiffWriter;

import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.plugin.ImageOrientation;

/**
 * {@link TestTiffImageWriter} implementation which write image with Packbits compression.
 *
 * @author Remi Marechal (Geomatys).
 * @see TiffImageWriteParam#compressionTypes
 */
public class PackbitsBandTiffWriterTest extends PackBitsTiffWriterTest {

    public PackbitsBandTiffWriterTest() throws IOException {
    }
    
    @Override
    protected strictfp ImageTypeSpecifier buildImageTypeSpecifier(int sampleBitsSize, int numBand, short photometricInterpretation, short sampleFormat) throws UnsupportedImageFormatException {
        final ImageTypeSpecifier imgTypeSpec = super.buildImageTypeSpecifier(sampleBitsSize, numBand, photometricInterpretation, sampleFormat); //To change body of generated methods, choose Tools | Templates.
        final ColorModel cm                  = imgTypeSpec.getColorModel();
        final int[] bankIndices = new int[numBand];
        int b = -1;
        while (++b < numBand) bankIndices[b] = b;
        final int[] bandOff = new int[numBand];
        final SampleModel sm = new BandedSampleModel(cm.getTransferType(), 1, 1, sampleBitsSize, bankIndices, bandOff);
        return new ImageTypeSpecifier(cm, sm);
    }

    @Override
    public void defaultColorMapTest() throws IOException {
        //-- do nothing has no sens to test color map
    }

    @Override
    protected void regionTest(String message, ImageOrientation imageOrientation) throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        //-- Short --//
        generalTest(message+" : 2 bands, type : Short.", fileTest, Short.SIZE, 2,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT, imageOrientation);
        
        //-- Int --//
        generalTest(message+" : 5 bands, type : Int.", fileTest, Integer.SIZE, 5,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT, imageOrientation);
        
        //-- Float --//
        generalTest(message+" : 5 bands, type : Float.", fileTest, Float.SIZE, 5,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP, imageOrientation);
        
        //-- Double --//
        generalTest(message+" : 5 bands, type : Double.", fileTest, Double.SIZE, 5,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP, imageOrientation);
        
        //-- RGB --//
        //-- type Byte RGB
        generalTest(message+" : 3 bands RGB, type : Byte.", fileTest, Byte.SIZE, 3,
                PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT, imageOrientation);
        generalTest(message+" : 4 bands RGB, type : Byte.", fileTest, Byte.SIZE, 4,
                PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT, imageOrientation);
    }
}
