/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.plugin.ImageOrientation;

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
    protected ImageTypeSpecifier buildImageTypeSpecifier(SampleType sampleType, int numBand,
                                                         PhotometricInterpretation photometricInterpretation)
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
    protected void regionTest(String message, ImageOrientation imageOrientation) throws IOException {
        super.regionTest(message, imageOrientation);
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        //-- Short --//
        generalTest(message+" : 5 bands, type : Short.", fileTest, SampleType.USHORT, 5,
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
}
