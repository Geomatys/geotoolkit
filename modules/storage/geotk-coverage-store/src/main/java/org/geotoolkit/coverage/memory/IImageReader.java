/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.memory;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

/**
 * Image reader for BufferedImage.
 * Just a wrapper class.
 * 
 * @author Johann sorel (Geomatys)
 */
public class IImageReader extends ImageReader{

    
    public IImageReader(ImageReaderSpi spi){
        super(spi);
    }
    
    private BufferedImage getImage() throws IOException{
        if(input instanceof BufferedImage){
            return (BufferedImage)input;
        }else{
            throw new IOException("Input is not a BufferedImage : " + input);
        }
    }
    
    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        return getImage().getWidth();
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        return getImage().getHeight();
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IOException {
        ImageTypeSpecifier spec = new ImageTypeSpecifier(getImage());
        return Collections.singleton(spec).iterator();
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        //defensive copy
        final BufferedImage image =  getImage();
        final WritableRaster rastercp = image.copyData(null);
        final BufferedImage copy = new BufferedImage(image.getColorModel(), rastercp, image.isAlphaPremultiplied(),new Hashtable<Object, Object>());
        return copy;
    }
    
    public static final class IISpi extends ImageReaderSpi{
        public static final IISpi INSTANCE = new IISpi();

        public IISpi() {
            inputTypes = new Class[]{BufferedImage.class};
        }
        
        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            return source instanceof BufferedImage;
        }

        @Override
        public ImageReader createReaderInstance(Object extension) throws IOException {
            return new IImageReader(this);
        }

        @Override
        public String getDescription(Locale locale) {
            return "Java Image Reader";
        }
        
    }
    
}
