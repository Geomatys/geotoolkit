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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import org.geotoolkit.image.io.plugin.TestTiffImageReaderWriter;

/**
 * Implement abstract method from {@link TestTiffImageReaderWriter} adapted for tests.
 *
 * @author Remi MArechal (Geomatys).
 */
public abstract class TestTiffImageWriter extends TestTiffImageReaderWriter {

    public TestTiffImageWriter(String compression) throws IOException {
        super(compression);
    }

    /**
     * {@inheritDoc }
     * 
     * In this implementation write the expected piece of image given by parameters and read image entirely.
     */
    @Override
    protected RenderedImage effectuateTest(File fileTest, RenderedImage sourceImage, Rectangle sourceRegion, 
    int sourceXSubsampling, int sourceYsubsampling, int sourceXOffset, int sourceYOffset, final Point destOffset) throws IOException {
        //-- write image completely --//
        writer.setOutput(fileTest);
        writerParam.setSourceRegion(null);
        writerParam.setSourceSubsampling(sourceXSubsampling, sourceYsubsampling, sourceXOffset, sourceYOffset);
        writerParam.setSourceRegion(sourceRegion);
        writerParam.setDestinationOffset(destOffset); 
        writer.write(sourceImage, writerParam);//-- write with param in case of tiled writing --//
        writer.dispose();
        
        //-- read expected piece of image --//
        reader.setInput(fileTest);
        final RenderedImage img = reader.read(0);
        reader.dispose();        
        return img;
    }
}
