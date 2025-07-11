/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.inputs.complex.ComplexToCoverageConverter;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ReferenceToGridCoverageResourceConverter extends AbstractReferenceInputConverter<GridCoverageResource> {

    private static ReferenceToGridCoverageResourceConverter INSTANCE;

    private ReferenceToGridCoverageResourceConverter() {
    }

    public static synchronized ReferenceToGridCoverageResourceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToGridCoverageResourceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverageResource> getTargetClass() {
        return GridCoverageResource.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return GridCoverageResource.
     */
    @Override
    public GridCoverageResource convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        try (final InputStream stream = getInputStreamFromReference(source)) {

            String encoding = null;
            if(params != null && params.get(ENCODING) != null) {
                encoding = (String) params.get(ENCODING);
            }
            byte[] imageData = null;
            try {

                 //decode form base64 stream
                if (encoding != null && encoding.equals(WPSEncoding.BASE64.getValue())) {
                    final String encodedImage = new String(stream.readAllBytes(), StandardCharsets.US_ASCII);
                    final byte[] byteData = Base64.getDecoder().decode(encodedImage.trim());
                    if (byteData != null && byteData.length > 0) {
                        if (WPSMimeType.IMG_GEOTIFF.val().equals(source.getMimeType()) || WPSMimeType.IMG_GEOTIFF_BIS.val().equals(source.getMimeType())) {
                            return ComplexToCoverageConverter.readGeotiffResource(byteData);
                        }
                        imageData = byteData;
                    }

                } else {
                    if (WPSMimeType.IMG_GEOTIFF.val().equals(source.getMimeType()) || WPSMimeType.IMG_GEOTIFF_BIS.val().equals(source.getMimeType())) {
                        return ComplexToCoverageConverter.readGeotiffResource(stream.readAllBytes());
                    }
                    imageData = stream.readAllBytes();
                }

                if (imageData != null) {
                    return ComplexToCoverageConverter.readAnyCoverageResource(imageData);
                } else {
                    throw new UnconvertibleObjectException("Error during image stream acquisition.");
                }

            } catch (MalformedURLException ex) {
                throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : Malformed url", ex);
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : Can't read coverage", ex);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("ReferenceType grid coverage invalid input : IO", ex);
            }
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error during image stream acquisition.");
        }
    }


}
