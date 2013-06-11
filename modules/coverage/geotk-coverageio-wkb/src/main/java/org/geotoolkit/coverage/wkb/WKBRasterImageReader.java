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
package org.geotoolkit.coverage.wkb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import org.geotoolkit.util.Version;
import org.apache.sis.util.ArraysExt;

/**
 * Draft java api image reader for WKB, used in postGIS 2 but can be used elsewhere.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WKBRasterImageReader extends ImageReader{

    public WKBRasterImageReader(ImageReaderSpi spi){
        super(spi);
    }

    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        final WKBRasterReader reader = new WKBRasterReader();
        if(input instanceof byte[]){
            return reader.read((byte[])input);
        }else if(input instanceof InputStream){
            return reader.read((InputStream)input);
        }
        throw new IOException("Unsupported input : "+input);
    }

    public static class Spi extends ImageReaderSpi {

        public Spi() {
            super();
            names           = new String[] {"PostGISWKBraster"};
            MIMETypes       = new String[] {"image/x-pgraster"};
            pluginClassName = "org.geotoolkit.coverage.postgresql.WKBRasterImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            writerSpiNames  = new String[] {};
            inputTypes = new Class[0];
            inputTypes      = ArraysExt.append(inputTypes, InputStream.class);
            inputTypes      = ArraysExt.append(inputTypes, byte[].class);
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new WKBRasterImageReader(this);
        }

        @Override
        public boolean canDecodeInput(final Object source) throws IOException {

            if(source instanceof byte[] || source instanceof InputStream){
                //TODO we must check more then that
                return true;
            }

            return false;
        }

        @Override
        public String getDescription(Locale locale) {
            return "Postgis WKB Raster reader.";
        }

    }

}
