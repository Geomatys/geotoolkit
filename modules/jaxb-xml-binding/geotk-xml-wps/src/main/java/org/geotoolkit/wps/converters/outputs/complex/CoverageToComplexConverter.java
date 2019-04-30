/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wps.converters.outputs.complex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import net.iharder.Base64;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.coverage.io.CoverageIO;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.Format;

/**
 * Convert an GridCoverage to Data using Base64 encoding.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CoverageToComplexConverter extends AbstractComplexOutputConverter<GridCoverage> {

    private static CoverageToComplexConverter INSTANCE;

    private CoverageToComplexConverter() {
    }

    public static synchronized CoverageToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoverageToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverage> getSourceClass() {
        return GridCoverage.class;
    }

    @Override
    public Data convert(GridCoverage source, Map<String, Object> params) throws UnconvertibleObjectException {
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

        final WPSMimeType wpsMime = WPSMimeType.customValueOf((String) tmpMime);
        if (!wpsMime.equals(WPSMimeType.IMG_GEOTIFF) && !wpsMime.equals(WPSMimeType.IMG_GEOTIFF_BIS)) {
            throw new UnconvertibleObjectException("Only support GeoTiff Base64 encoding.");
        }
        final Object tmpEncoding = params.get(ENCODING);

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            CoverageIO.write(source, "GEOTIFF", baos);
            baos.flush();
            byte[] bytesOut = baos.toByteArray();
            return new Data(new Format((String)((tmpEncoding instanceof String)? tmpEncoding : null), mime, null, null), Base64.encodeBytes(bytesOut));

        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }
    }

}
