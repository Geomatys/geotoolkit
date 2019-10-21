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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v200.Reference;
;

/**
 * Implementation of ObjectConverter to convert a reference into a GridCoverageReader.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToGridCoverageReaderConverter extends AbstractReferenceInputConverter<GridCoverageReader> {

    private static ReferenceToGridCoverageReaderConverter INSTANCE;

    private ReferenceToGridCoverageReaderConverter() {
    }

    public static synchronized ReferenceToGridCoverageReaderConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToGridCoverageReaderConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverageReader> getTargetClass() {
        return GridCoverageReader.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return GridCoverageReader.
     */
    @Override
    public GridCoverageReader convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final InputStream stream = getInputStreamFromReference(source);

        String encoding = null;
        if(params != null && params.get(ENCODING) != null) {
            encoding = (String) params.get(ENCODING);
        }
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
                final ImageReader reader;
                if (source.getMimeType() != null) {
                    reader = XImageIO.getReaderByMIMEType(source.getMimeType(), imageStream, null, null);
                } else {
                    reader = XImageIO.getReader(imageStream, null, Boolean.FALSE);
                }
                return CoverageIO.createSimpleReader(reader);
            } else {
                throw new UnconvertibleObjectException("Error during image stream acquisition.");
            }

        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : Malformed url", ex);
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : Can't read coverage", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : IO", ex);
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
