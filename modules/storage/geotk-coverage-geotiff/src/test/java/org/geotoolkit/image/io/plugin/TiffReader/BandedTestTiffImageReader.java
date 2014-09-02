/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2014, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin.TiffReader;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.plugin.ImageOrientation;
import org.junit.Test;

/**
 * Effectuate all same test than super class with image build with {@link BandedSampleModel}.
 * 
 * @author Remi Marechal (Geomatys) 
 */
public strictfp abstract class BandedTestTiffImageReader extends TestTiffImageReader {

    public BandedTestTiffImageReader(String compression) throws IOException {
        super(compression);
    }
    
    /**
     * {@inheritDoc }
     * Same than super class 
     */
    @Override
    protected ImageTypeSpecifier buildImageTypeSpecifier(int sampleBitsSize, int numBand, short photometricInterpretation, short sampleFormat) throws UnsupportedImageFormatException {
        return buildImageTypeSpecifier(sampleBitsSize, numBand, photometricInterpretation, sampleFormat, (short) 2);
    }

    /**
     * {@inheritDoc }
     * Same than super class 
     */
    @Override
    public void defaultColorMapTest() throws IOException {
        //-- do nothing has no sens to test color map
    }
   
    /**
     * {@inheritDoc }
     * Same than super class 
     */
    @Override
    protected void regionTest(String message, ImageOrientation imageOrientation) throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        //-- Short --//
        generalTest(message+" : 5 bands, type : Short.", fileTest, Short.SIZE, 5,
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
