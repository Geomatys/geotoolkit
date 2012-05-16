/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.references;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;

/**
 * Implementation of ObjectConverter to convert a {@link RenderedImage image} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class RenderedImageToReferenceConverter extends AbstractReferenceOutputConverter {

    private static RenderedImageToReferenceConverter INSTANCE;

    private RenderedImageToReferenceConverter() {
    }

    public static synchronized RenderedImageToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RenderedImageToReferenceConverter();
        }
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final Map<String, Object> source) throws NonconvertibleObjectException {
        
        if (source.get(OUT_TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        final Object data = source.get(OUT_DATA);
        
        if (data == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(data instanceof BufferedImage) && !(data instanceof RenderedImage)) {
            throw new NonconvertibleObjectException("The output data is not an instance of RenderedImage.");
        }
        
        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) source.get(OUT_IOTYPE));
        ReferenceType reference = null ;
        
        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        reference.setMimeType((String) source.get(OUT_MIME));
        reference.setEncoding((String) source.get(OUT_ENCODING));
        reference.setSchema((String) source.get(OUT_SCHEMA));

        final String mime = (String) source.get(OUT_MIME) != null ? (String) source.get(OUT_MIME) : "image/png";
        
        reference.setMimeType(mime);
        reference.setEncoding((String) source.get(OUT_ENCODING));
        reference.setSchema((String) source.get(OUT_SCHEMA));

        final String randomFileName = UUID.randomUUID().toString();
        ImageWriter writer = null;
        try {
            //create file
            final File imageFile = new File((String) source.get(OUT_TMP_DIR_PATH), randomFileName);
            final RenderedImage image = (RenderedImage) data;
            writer = XImageIO.getWriterByMIMEType(mime, imageFile, image);
            writer.write(image);
            reference.setHref((String) source.get(OUT_TMP_DIR_URL) + "/" + randomFileName);
            
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Error occure during image writing.", ex);
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
        
        return reference;
    }
}
