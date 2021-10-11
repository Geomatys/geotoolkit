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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;

/**
 * Implementation of ObjectConverter to convert a {@link GridCoverage coverage} into a {@link Reference reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class CoverageToReferenceConverter extends AbstractReferenceOutputConverter<GridCoverage> {

    private static CoverageToReferenceConverter INSTANCE;

    private CoverageToReferenceConverter() {
    }

    public static synchronized CoverageToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoverageToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverage> getSourceClass() {
        return GridCoverage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reference convert(final GridCoverage source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        Reference reference = new Reference();

        final String encodingStr = (String) params.get(ENCODING);
        final String mimeStr = (String) params.get(MIME) != null ? (String) params.get(MIME) : WPSMimeType.IMG_GEOTIFF.val();
        final WPSMimeType mime = WPSMimeType.customValueOf(mimeStr);

        reference.setMimeType(mimeStr);
        reference.setEncoding(encodingStr);
        reference.setSchema((String) params.get(SCHEMA));

        final String formatName;
        final String[] formatNames = XImageIO.getFormatNamesByMimeType(mimeStr, true, true);
        formatName = (formatNames.length < 1)? "GEOTIFF" : formatNames[0];
        final String randomFileName = UUID.randomUUID().toString();

        try {

            final Path imageFile = buildPath(params, randomFileName);

            if (encodingStr != null && encodingStr.equals(WPSEncoding.BASE64.getValue())) {
                try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    CoverageIO.write(source, formatName, baos);
                    baos.flush();
                    byte[] bytesOut = baos.toByteArray();
                    IOUtilities.writeString(Base64.getEncoder().encodeToString(bytesOut), imageFile);
                }

            } else {
                //Note : do not do OutputStream out = Files.newOutputStream(imageFile, StandardOpenOption.CREATE, WRITE, TRUNCATE_EXISTING)
                //Most coverage writer do not support stream writing properly, it is better to work with a file.
                //This also avoid keeping large files in memory if byte buffer seeking is needed by the writer.
                Files.deleteIfExists(imageFile);
                CoverageIO.write(source, formatName, imageFile);
            }

            final String relLoc = getRelativeLocation(imageFile, params);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +relLoc);

        } catch (IOException | DataStoreException ex) {
            throw new UnconvertibleObjectException("Error during writing the coverage in the output file.",ex);
        }

        return reference;
    }

}
