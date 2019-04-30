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
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.iharder.Base64;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v200.Reference;
;

/**
 * Implementation of ObjectConverter to convert a reference into a GridCoverage.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToGridCoverage2DConverter extends AbstractReferenceInputConverter<GridCoverage> {

    private static ReferenceToGridCoverage2DConverter INSTANCE;

    private ReferenceToGridCoverage2DConverter() {
    }

    public static synchronized ReferenceToGridCoverage2DConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToGridCoverage2DConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverage> getTargetClass() {
        return GridCoverage.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return GridCoverage.
     */
    @Override
    public GridCoverage convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

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
                final byte[] byteData = Base64.decode(encodedImage.trim());
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
                return CoverageIO.read(reader);
            } else {
                throw new UnconvertibleObjectException("Error during image stream acquisition.");
            }

        } catch (IOException ex) {
            throw new UnconvertibleObjectException("ReferenceType coverage invalid input : IO", ex);
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException("ReferenceType coverage invalid input : Can't read coverage", ex);
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
