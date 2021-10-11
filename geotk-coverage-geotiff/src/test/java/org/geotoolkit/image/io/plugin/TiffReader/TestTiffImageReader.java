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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import org.geotoolkit.image.io.plugin.TestTiffImageReaderWriter;

/**
 * Implement method to be in accordance with Reader test.
 *
 * @author remi Marechal (geomatys).
 */
public abstract strictfp class TestTiffImageReader extends TestTiffImageReaderWriter {

    public TestTiffImageReader(String compression) throws IOException {
        super(compression);
    }

    /**
     * {@inheritDoc }
     *
     * In this implementation write image entirely and read the expected piece of image given by parameters.
     */
    @Override
    protected RenderedImage effectuateTest(File fileTest, RenderedImage sourceImage, Rectangle sourceRegion,
    int sourceXSubsample, int sourceYsubsampling, int sourceXOffset, int sourceYOffset, final Point destOffset) throws IOException {
        //-- write image completely --//
        writer.setOutput(fileTest);
        writer.write(sourceImage, writerParam);//-- write with param in case of tiled writing --//
        writer.dispose();

        //-- prepare ImageReaderParam --//
        readerParam.setSourceRegion(null);
        readerParam.setSourceSubsampling(sourceXSubsample, sourceYsubsampling, sourceXOffset, sourceYOffset);
        readerParam.setSourceRegion(sourceRegion);
        readerParam.setDestinationOffset(destOffset);

        //-- read expected piece of image --//
        reader.setInput(fileTest);
        final RenderedImage img = reader.read(0, readerParam);
        reader.dispose();
        return img;
    }
}
