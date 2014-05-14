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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;

import org.geotoolkit.image.io.InputStreamAdapter;
import org.geotoolkit.util.Utilities;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.internal.io.IOUtilities;

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
        }else if(input instanceof ImageInputStream){
            return reader.read(new InputStreamAdapter((ImageInputStream)input));
        }else{
            InputStream stream;
            if(input instanceof InputStream){
                stream = (InputStream) input;
            }else{
                stream = IOUtilities.open(input);
            }
            final BufferedImage image = reader.read(stream);
            stream.close();
            return image;
        }
    }

    public static class Spi extends ImageReaderSpi {

        public Spi() {
            super();
            names           = new String[] {"PostGISWKBraster"};
            MIMETypes       = new String[] {"image/x-pgraster"};
            pluginClassName = "org.geotoolkit.coverage.wkb.WKBRasterImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            writerSpiNames  = new String[] {"PostGISWKBraster"};
            inputTypes      = new Class[0];
            inputTypes      = ArraysExt.append(inputTypes, InputStream.class);
            inputTypes      = ArraysExt.append(inputTypes, ImageInputStream.class);
            inputTypes      = ArraysExt.append(inputTypes, File.class);
            inputTypes      = ArraysExt.append(inputTypes, URL.class);
            inputTypes      = ArraysExt.append(inputTypes, URI.class);
            inputTypes      = ArraysExt.append(inputTypes, Path.class);
            inputTypes      = ArraysExt.append(inputTypes, byte[].class);
            suffixes        = new String[0];
            suffixes        = ArraysExt.append(suffixes, "wkb");
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

    final class InputStreamAdapter extends InputStream {
    /**
     * The wrapped image input stream.
     */
    private final ImageInputStream input;

    /**
     * Constructs a new input stream.
     */
    public InputStreamAdapter(final ImageInputStream input) {
        this.input = input;
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        return input.read();
    }

    /**
     * Reads some number of bytes from the input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return input.read(b);
    }

    /**
     * Reads up to {@code len} bytes of data from the input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return input.read(b, off, len);
    }

    /**
     * Skips over and discards {@code n} bytes of data from this input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long skip(final long n) throws IOException {
        return input.skipBytes(n);
    }

    /**
     * Returns always {@code true}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the current position in this input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void mark(final int readlimit) {
        input.mark();
    }

    /**
     * Repositions this stream to the position at the time
     * the {@code mark} method was last called.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void reset() throws IOException {
        input.reset();
    }

    /**
     * Closes this input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
    }

}
