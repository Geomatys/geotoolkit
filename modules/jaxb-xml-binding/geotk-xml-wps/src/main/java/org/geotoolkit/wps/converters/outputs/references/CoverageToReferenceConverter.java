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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;
import net.iharder.Base64;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Implementation of ObjectConverter to convert a {@link GridCoverage2D coverage} into a {@link Reference reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class CoverageToReferenceConverter extends AbstractReferenceOutputConverter<GridCoverage2D> {

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
    public Class<GridCoverage2D> getSourceClass() {
        return GridCoverage2D.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reference convert(final GridCoverage2D source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);

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

            final Path imageFile = Paths.get((String) params.get(TMP_DIR_PATH), randomFileName);

            if (encodingStr != null && encodingStr.equals(WPSEncoding.BASE64.getValue())) {
                try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    CoverageIO.write(source, formatName, baos);
                    baos.flush();
                    byte[] bytesOut = baos.toByteArray();
                    IOUtilities.writeString(Base64.encodeBytes(bytesOut), imageFile);
                }

            } else {
                try (OutputStream out = Files.newOutputStream(imageFile, StandardOpenOption.CREATE, WRITE, TRUNCATE_EXISTING)) {
                    CoverageIO.write(source, formatName, out);
                }
            }

            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +randomFileName);

        } catch (IOException | CoverageStoreException ex) {
            throw new UnconvertibleObjectException("Error during writing the coverage in the output file.",ex);
        }

        return reference;
    }

}
