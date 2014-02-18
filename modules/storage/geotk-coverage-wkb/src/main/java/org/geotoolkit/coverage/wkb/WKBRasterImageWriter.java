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
package org.geotoolkit.coverage.wkb;

import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WKBRasterImageWriter extends ImageWriter{

    public WKBRasterImageWriter(ImageWriterSpi spi){
        super(spi);
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return new SpatialMetadata(true, this, null);
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
       return new SpatialMetadata(false, this, null);
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
        return new SpatialMetadata(true, this, null);
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        return new SpatialMetadata(false, this, null);
    }

    @Override
    public boolean canWriteRasters() {
        return true;
    }

    @Override
    public void write(IIOImage image) throws IOException {
        final Raster ri = image.getRaster();
        final WKBRasterWriter writer = new WKBRasterWriter();

        final Object out = getOutput();
        if(out instanceof ImageOutputStream){
            final ImageOutputStream stream = (ImageOutputStream) out;
            final byte[] data = writer.write(ri, new AffineTransform(), 0);
            stream.write(data);

        }else{
            final OutputStream stream = IOUtilities.openWrite(getOutput());
            writer.write(ri, new AffineTransform(), 0, stream);
            stream.flush();
            stream.close();
        }
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
        final RenderedImage ri = image.getRenderedImage();
        final WKBRasterWriter writer = new WKBRasterWriter();

        final Object out = getOutput();
        if(out instanceof ImageOutputStream){
            final ImageOutputStream stream = (ImageOutputStream) out;
            final byte[] data = writer.write(ri, new AffineTransform(), 0);
            stream.write(data);

        }else{
            final OutputStream stream = IOUtilities.openWrite(getOutput());
            writer.write(ri, new AffineTransform(), 0, stream);
            stream.flush();
            stream.close();
        }

    }

    public static class Spi extends ImageWriterSpi {

        public Spi() {
            super();
            names           = new String[] {"PostGISWKBraster"};
            MIMETypes       = new String[] {"image/x-pgraster"};
            pluginClassName = "org.geotoolkit.coverage.wkb.WKBRasterImageWriter";
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            readerSpiNames  = new String[] {"PostGISWKBraster"};
            outputTypes     = new Class[0];
            outputTypes     = ArraysExt.append(outputTypes, OutputStream.class);
            outputTypes     = ArraysExt.append(outputTypes, File.class);
            outputTypes     = ArraysExt.append(outputTypes, URL.class);
            outputTypes     = ArraysExt.append(outputTypes, URI.class);
            outputTypes     = ArraysExt.append(outputTypes, Path.class);
            outputTypes     = ArraysExt.append(outputTypes, ImageOutputStream.class);
            suffixes        = new String[0];
            suffixes        = ArraysExt.append(suffixes, "wkb");
        }

        @Override
        public String getDescription(Locale locale) {
            return "Postgis WKB Raster writer.";
        }

        @Override
        public boolean canEncodeImage(ImageTypeSpecifier type) {
            return true;
        }

        @Override
        public ImageWriter createWriterInstance(Object extension) throws IOException {
            return new WKBRasterImageWriter(this);
        }

    }

}
