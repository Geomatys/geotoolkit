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
package org.geotoolkit.client.map;

import java.awt.Point;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.client.Request;
import org.geotoolkit.coverage.DefaultTileReference;
import org.geotoolkit.image.io.XImageIO;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RequestTileReference extends DefaultTileReference {

    public RequestTileReference(ImageReaderSpi spi, Request input, int imageIndex, Point position) {
        super(spi, input, imageIndex, position);
    }



    @Override
    public ImageReader getImageReader() throws IOException {

        ImageReaderSpi spi = this.spi;
        ImageReader reader = null;

        if(spi == null){

            reader = XImageIO.getReader(((Request)input).getResponseStream(), Boolean.TRUE, Boolean.TRUE);
            if (reader.getClass().getName().startsWith("com.sun.media")) {
                final ImageInputStream ninput = ImageIO.createImageInputStream(((Request)input).getResponseStream());
                reader = XImageIO.getReader(ninput, Boolean.TRUE, Boolean.TRUE);
                ninput.close();
            }
            spi = reader.getOriginatingProvider();
        }

        final Class[] supportedTypes = spi.getInputTypes();
        Object in = null;

        //try to reuse input if it's supported
        final Object inputTmp = ((Request)input).getResponseStream();
        for(Class type : supportedTypes){
            if(type.isInstance(inputTmp)){
                in = inputTmp;
                break;
            }
        }

        //use default image stream if necessary
        if(in == null){
            in = ImageIO.createImageInputStream(inputTmp);
        }

        if(reader == null){
            reader = spi.createReaderInstance();
        }

        reader.setInput(in, true, true);
        return reader;
    }
}
