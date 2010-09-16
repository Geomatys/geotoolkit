/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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

package org.geotoolkit.util;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.geotoolkit.image.io.XImageIO;


/**
 * Useful methods for image writing.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class ImageIOUtilities {

    private ImageIOUtilities(){}

    public static void writeImage(final RenderedImage image,
            final String mime, Object output) throws IOException{

        final ImageWriter writer = getImageWriter(image, mime, output);
        final ImageWriterSpi spi = writer.getOriginatingProvider();

        ImageOutputStream stream = null;
        if (!isValidType(spi.getOutputTypes(), output)) {
            stream = ImageIO.createImageOutputStream(output);
            output = stream;
        }
        writer.setOutput(output);
        writer.write(image);
        writer.dispose();
        if (stream != null) {
            stream.close();
        }

    }

    public static void writeImage(final RenderedImage image, Object output, ImageWriter writer) throws IOException{

        final ImageWriterSpi spi = writer.getOriginatingProvider();

        ImageOutputStream stream = null;
        if (!isValidType(spi.getOutputTypes(), output)) {
            stream = ImageIO.createImageOutputStream(output);
            output = stream;
        }
        writer.setOutput(output);
        writer.write(image);
        writer.dispose();
        if (stream != null) {
            stream.close();
        }

    }

    public static ImageWriter getImageWriter(final RenderedImage image, String mime, Object output) throws IOException{
        if(image == null) throw new NullPointerException("Image can not be null");

        int n = 0;
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mime);
        while (writers.hasNext()) {
            final ImageWriter writer = writers.next();
            final ImageWriterSpi spi = writer.getOriginatingProvider();
            if (spi.canEncodeImage(image)) {
                return writer;
            }
            n++;
        }
        throw new IOException("Can not write the image for the MIME type : " + mime +
                ". Found " + n + " writers, but none can encode the image. "
                + "Available MIME type are " + Arrays.toString(ImageIO.getWriterMIMETypes()));
    }

    /**
     * Check if the provided object is an instance of one of the given classes.
     */
    public static boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the mime type matching the extension of an image file.
     * For example, for a file "my_image.png" it will return "image/png", in most cases.
     *
     * @param extension The extension of an image file.
     * @return The mime type for the extension specified.
     *
     * @throws IIOException if no image reader are able to handle the extension given.
     */
    public static String fileExtensionToMimeType(final String extension) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] suffixes = reader.getFileSuffixes();
            for (String suffixe : suffixes) {
                if (extension.equalsIgnoreCase(suffixe)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the extension specified: "+ extension);
    }

    /**
     * Returns the mime type matching the format name of an image file.
     * For example, for a format name "png" it will return "image/png", in most cases.
     *
     * @param format name The format name of an image file.
     * @return The mime type for the format name specified.
     *
     * @throws IIOException if no image reader are able to handle the format name given.
     */
    public static String formatNameToMimeType(final String formatName) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] formats = reader.getFormatNames();
            for (String format : formats) {
                if (formatName.equalsIgnoreCase(format)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the format name specified: "+ formatName);
    }

    /**
     * Returns the format name matching the mime type of an image file.
     * For example, for a mime type "image/png" it will return "png", in most cases.
     *
     * @param mimeType The mime type of an image file.
     * @return The format name for the mime type specified.
     *
     * @throws IIOException if no image reader are able to handle the mime type given.
     */
    public static String mimeTypeToFormatName(final String mimeType) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] mimes = reader.getMIMETypes();
            for (String mime : mimes) {
                if (mimeType.equalsIgnoreCase(mime)) {
                    final String[] formats = reader.getFormatNames();
                    if (formats != null && formats.length > 0) {
                        return formats[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the mime type specified: "+ mimeType);
    }

}
