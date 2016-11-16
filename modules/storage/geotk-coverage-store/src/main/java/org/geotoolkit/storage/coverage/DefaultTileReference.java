/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Point;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.geotoolkit.image.io.XImageIO;

/**
 * Default implementation of a TileReference
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTileReference implements TileReference{

    protected final ImageReaderSpi spi;
    protected final Object input;
    protected final int imageIndex;
    protected final Point position;

    public DefaultTileReference(ImageReaderSpi spi, Object input, int imageIndex, Point position) {
        this.spi = spi;
        this.input = input;
        this.imageIndex = imageIndex;
        this.position = position;
    }

    @Override
    public ImageReader getImageReader() throws IOException {

        ImageReaderSpi spi = this.spi;
        ImageReader reader = null;

        if (spi == null && input != null) {
            reader = XImageIO.getReader(input, Boolean.FALSE, Boolean.FALSE);
            spi = reader.getOriginatingProvider();
        }

        if (spi == null) {
            //could not find a proper reader for input
            throw new IOException("Could not find image reader spi for input : "+input);
        }
        
        Object in = XImageIO.toSupportedInput(spi, input);

        if(reader == null) {
            reader = spi.createReaderInstance();
        }

        reader.setInput(in, true, true);
        return reader;
    }

    @Override
    public ImageReaderSpi getImageReaderSpi() {
        return spi;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public Point getPosition() {
        return position;
    }

}
