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
public class RenderedImageToReferenceConverter extends AbstractReferenceOutputConverter<RenderedImage> {

    private static RenderedImageToReferenceConverter INSTANCE;

    private RenderedImageToReferenceConverter() {
    }

    public static synchronized RenderedImageToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RenderedImageToReferenceConverter();
        }
        return INSTANCE;
    }
    
    @Override
    public Class<? super RenderedImage> getSourceClass() {
        return RenderedImage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceType convert(final RenderedImage source, final Map<String, Object> params) throws NonconvertibleObjectException {
        
        if (params.get(TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        if (source == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof BufferedImage) && !(source instanceof RenderedImage)) {
            throw new NonconvertibleObjectException("The output data is not an instance of RenderedImage.");
        }
        
        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        ReferenceType reference = null;
        
        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        final String mime = (String) params.get(MIME) != null ? (String) params.get(MIME) : "image/png";
        
        reference.setMimeType(mime);
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        final String randomFileName = UUID.randomUUID().toString();
        ImageWriter writer = null;
        try {
            //create file
            final File imageFile = new File((String) params.get(TMP_DIR_PATH), randomFileName);
            writer = XImageIO.getWriterByMIMEType(mime, imageFile, source);
            writer.write(source);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" + randomFileName);
            
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
