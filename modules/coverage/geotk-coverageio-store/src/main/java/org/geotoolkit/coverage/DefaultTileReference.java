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
package org.geotoolkit.coverage;

import java.awt.Point;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.geotoolkit.image.io.XImageIO;

/**
 * Default implementation of a TileReference
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTileReference implements TileReference{

    private final ImageReaderSpi spi;
    private final Object input;
    private final int imageIndex;
    private final Point position;

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
        
        if(spi == null){
            reader = XImageIO.getReader(input, Boolean.TRUE, Boolean.TRUE);
            spi = reader.getOriginatingProvider();
        }
                
        final Class[] supportedTypes = spi.getInputTypes();
        Object in = null;
        
        //try to reuse input if it's supported
        for(Class type : supportedTypes){
            if(type.isInstance(input)){
                in = input;
                break;
            }
        }
        
        //use default image stream if necessary
        if(in == null){
            in = ImageIO.createImageInputStream(input);
        }
        
        if(reader == null){
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
