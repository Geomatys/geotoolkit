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
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import net.iharder.Base64;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v100.ReferenceType;

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
    public Class<? extends GridCoverageReader> getTargetClass() {
        return GridCoverageReader.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return GridCoverageReader.
     */
    @Override
    public GridCoverageReader convert(final ReferenceType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        final InputStream stream = getInputStreamFromReference(source);
        
        String encoding = null;
        if(params != null && params.get(ENCODING) != null) {
            encoding = (String) params.get(ENCODING);
        }
        ImageInputStream imageStream = null;
        try {

             //decode form base64 stream
            if (encoding != null && encoding.equals(WPSEncoding.BASE64.getValue())) {
                final String encodedImage = FileUtilities.getStringFromStream(stream);
                final byte[] byteData = Base64.decode(encodedImage.trim());
                if (byteData != null && byteData.length > 0) {
                    final InputStream is = new ByteArrayInputStream(byteData);
                    if (is != null) {
                        imageStream = ImageIO.createImageInputStream(is);
                    }
                }
                
            } else {
                imageStream = ImageIO.createImageInputStream(stream);
            }
            
            if (imageStream != null) {
                return CoverageIO.createSimpleReader(imageStream);
            } else {
                throw new NonconvertibleObjectException("Error during image stream acquisition.");
            }
            
        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException("Reference grid coverage invalid input : Malformed url", ex);
        } catch (CoverageStoreException ex) {
            throw new NonconvertibleObjectException("Reference grid coverage invalid input : Can't read coverage", ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Reference grid coverage invalid input : IO", ex);
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