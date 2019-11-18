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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v200.Reference;;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class ReferenceToRenderedImageConverter extends AbstractReferenceInputConverter<RenderedImage> {

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
    public Class<RenderedImage> getTargetClass() {
        return RenderedImage.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return RenderedImage.
     */
    @Override
    public RenderedImage convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final InputStream stream = getInputStreamFromReference(source);

        final String encoding = (String)params.get(ENCODING);

        ImageInputStream imageStream = null;
        try {

            //decode form base64 stream
            if (encoding != null && encoding.equals(WPSEncoding.BASE64.getValue())) {
                final String encodedImage = IOUtilities.toString(stream);
                final byte[] byteData = Base64.getDecoder().decode(encodedImage.trim());
                if (byteData != null && byteData.length > 0) {
                    try (InputStream is = new ByteArrayInputStream(byteData)) {
                        imageStream = ImageIO.createImageInputStream(is);
                    }
                }

            } else {
                imageStream = ImageIO.createImageInputStream(stream);
            }

            if (imageStream != null) {
                 return ImageIO.read(imageStream);
            } else {
                throw new UnconvertibleObjectException("Error during image stream acquisition.");
            }

        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("ReferenceType image invalid URL : Malformed url", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("ReferenceType image invalid input : IO", ex);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Error during release the image stream.", ex);
                }
            }
        }
    }
}
