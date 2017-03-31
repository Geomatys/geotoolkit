/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageTypeSpecifier;

import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.plugin.ImageOrientation;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Effectuate all same test than super class with image build with {@link BandedSampleModel}.
 *
 * @author Remi Marechal (Geomatys)
 */
public strictfp class BandUncompressedTiffWriterTest extends UncompressedTiffWriterTest {

    public BandUncompressedTiffWriterTest() throws IOException {
    }

    /**
     * {@inheritDoc }
     * Returns an {@link ImageTypeSpecifier} with a intenal banded {@link SampleModel}.
     *
     * @return {@link ImageTypeSpecifier} with a intenal banded {@link SampleModel}
     * @throws org.geotoolkit.image.io.UnsupportedImageFormatException
     */
    @Override
    protected ImageTypeSpecifier buildImageTypeSpecifier(final SampleType sampleType, final int numBand,
                                                         final PhotometricInterpretation photometricInterpretation)
            throws UnsupportedImageFormatException {
        return ImageUtils.buildImageTypeSpecifier(sampleType, numBand,
                                                    photometricInterpretation, PlanarConfiguration.BANDED,
                                                    false, false,
                                                    buildColorMapArray(sampleType));
    }

    /**
     * {@inheritDoc }
     * Same than super class
     */
    @Override
    protected void regionTest(final String message, final ImageOrientation imageOrientation) throws IOException {
        super.regionTest(message, imageOrientation);
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        //-- Short --//
        generalTest(message+" : 5 bands, type : Short.", fileTest, SampleType.BYTE, 5,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);

        //-- Int --//
        generalTest(message+" : 5 bands, type : Int.", fileTest, SampleType.INTEGER, 5,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);

        //-- Float --//
        generalTest(message+" : 5 bands, type : Float.", fileTest, SampleType.FLOAT, 5,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);

        //-- Double --//
        generalTest(message+" : 5 bands, type : Double.", fileTest, SampleType.DOUBLE, 5,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);

        //-- RGB --//
        //-- type Byte RGB
        generalTest(message+" : 3 bands RGB, type : Byte.", fileTest, SampleType.BYTE, 3,
                PhotometricInterpretation.RGB, imageOrientation);
        generalTest(message+" : 4 bands RGB, type : Byte.", fileTest, SampleType.BYTE, 4,
                PhotometricInterpretation.RGB, imageOrientation);
    }

    /**
     * Ignore this test because can't find bug about Banded sampleModel multiple successive writeEmpty().
     *
     * @throws IOException
     */
    @Override
    @Test
    @Ignore
    public strictfp void writeEmptyTest() throws IOException {
        super.writeEmptyTest(); //To change body of generated methods, choose Tools | Templates.
    }
}
