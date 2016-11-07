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

import net.iharder.Base64;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSIO;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.geotoolkit.wps.converters.WPSObjectConverter.IOTYPE;
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;

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
    public Class<RenderedImage> getSourceClass() {
        return RenderedImage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reference convert(final RenderedImage source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof BufferedImage) && !(source instanceof RenderedImage)) {
            throw new UnconvertibleObjectException("The output data is not an instance of RenderedImage.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);


        final String encoding = (String) params.get(ENCODING);

        final String mime = (String) params.get(MIME) != null ? (String) params.get(MIME) : "image/png";
        final String formatName = mime.substring(mime.indexOf("/")+1).toUpperCase();

        reference.setMimeType(mime);
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        final String randomFileName = UUID.randomUUID().toString();
        try {

            final Path imageFile = Paths.get((String) params.get(TMP_DIR_PATH), randomFileName);

            if (encoding != null && encoding.equals(WPSEncoding.BASE64.getValue())) {
                try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(source, formatName, baos);
                    baos.flush();
                    byte[] bytesOut = baos.toByteArray();
                    IOUtilities.writeString(Base64.encodeBytes(bytesOut), imageFile);
                }

            } else {
                try (OutputStream out = Files.newOutputStream(imageFile, StandardOpenOption.CREATE, WRITE, TRUNCATE_EXISTING)) {
                    ImageIO.write(source, formatName, out);
                }
            }

            reference.setHref((String) params.get(TMP_DIR_URL) + "/" + randomFileName);

        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error occured during image writing.", ex);
        }

        return reference;
    }

}
