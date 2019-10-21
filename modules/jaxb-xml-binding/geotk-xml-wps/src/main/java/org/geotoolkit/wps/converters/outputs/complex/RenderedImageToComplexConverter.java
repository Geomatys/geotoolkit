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
package org.geotoolkit.wps.converters.outputs.complex;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.sis.util.UnconvertibleObjectException;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Convert an RenderedImage to Data using Base64 encoding.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class RenderedImageToComplexConverter extends AbstractComplexOutputConverter<RenderedImage>  {

    private static RenderedImageToComplexConverter INSTANCE;

    private RenderedImageToComplexConverter() {
    }

    public static synchronized RenderedImageToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RenderedImageToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<RenderedImage> getSourceClass() {
        return RenderedImage.class;
    }

    @Override
    public Data convert(RenderedImage source, Map<String, Object> params) throws UnconvertibleObjectException {
        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        } else if (params == null) {
            throw new UnconvertibleObjectException("Not enough information about data format");
        }

        final Object tmpMime = params.get(MIME);
        final String mime;
        if (tmpMime instanceof String) {
            mime = (String) tmpMime;
        } else {
            throw new UnconvertibleObjectException("No valid mime type given. We cannot determine output image format");
        }

        final Data complex = new Data();
        complex.setMimeType(mime);

        final Object tmpEncoding = params.get(ENCODING);
        if (tmpEncoding instanceof String) {
            complex.setEncoding((String) tmpEncoding);
        }

        final String formatName = mime.substring(mime.indexOf("/")+1).toUpperCase();
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(source, formatName, baos);
            baos.flush();
            byte[] bytesOut = baos.toByteArray();
            complex.getContent().add(Base64.getEncoder().encodeToString(bytesOut));
            baos.close();
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }
        return complex;
    }

}
