/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.references;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.ReferenceType;

/**
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class ReferenceToRenderedImageConverter extends AbstractReferenceInputConverter {

    private static ReferenceToRenderedImageConverter INSTANCE;

    private ReferenceToRenderedImageConverter() {
    }

    public static synchronized ReferenceToRenderedImageConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToRenderedImageConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends Object> getTargetClass() {
        return RenderedImage.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return RenderedImage.
     */
    @Override
    public RenderedImage convert(final ReferenceType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        final InputStream stream = getInputStreamFromReference(source);

        ImageInputStream imageStream = null;
        try {

            imageStream = ImageIO.createImageInputStream(stream);
            final ImageReader imageReader = XImageIO.getReader(imageStream, true, true);
            //read the first image.
            return imageReader.read(0);
            
        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException("Reference image invalid URL : Malformed url", ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Reference image invalid input : IO", ex);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException ex) {
                    throw new NonconvertibleObjectException("Error during release the image stream.", ex);
                }
            }
        }
    }
}
