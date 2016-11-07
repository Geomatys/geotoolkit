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
import java.util.Map;
import javax.imageio.ImageIO;
import net.iharder.Base64;
import org.apache.sis.util.UnconvertibleObjectException;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.geotoolkit.wps.xml.WPSXmlFactory;

/**
 * Convert an RenderedImage to ComplexDataType using Base64 encoding.
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
    public ComplexDataType convert(RenderedImage source, Map<String, Object> params) throws UnconvertibleObjectException {

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof RenderedImage)) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of RenderedImage.");
        }

        final String mime = (String) params.get(MIME);
        final String encoding = (String) params.get(ENCODING);

        if (mime == null) {
            throw new UnconvertibleObjectException("MimeType should be defined to encode image in right format in Base64.");
        }

        if (!encoding.equals(WPSEncoding.BASE64.getValue())) {
            throw new UnconvertibleObjectException("Encoding should be in Base64 for complex request.");
        }
        
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        } 
        final ComplexDataType complex = WPSXmlFactory.buildComplexDataType(wpsVersion, encoding,mime, null);

        final String formatName = mime.substring(mime.indexOf("/")+1).toUpperCase();
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(source, formatName, baos);
            baos.flush();
            byte[] bytesOut = baos.toByteArray();
            complex.getContent().add(Base64.encodeBytes(bytesOut));
            baos.close();
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }
        return complex;
    }

}
