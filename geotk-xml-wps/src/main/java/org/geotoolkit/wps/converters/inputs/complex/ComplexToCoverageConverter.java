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
package org.geotoolkit.wps.converters.inputs.complex;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Convert an base64 encoded coverage into a GridCoverage.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class ComplexToCoverageConverter extends AbstractComplexInputConverter<GridCoverage> {

    private static ComplexToCoverageConverter INSTANCE;

    private ComplexToCoverageConverter(){
    }

    public static synchronized ComplexToCoverageConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToCoverageConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<GridCoverage> getTargetClass() {
        return GridCoverage.class;
    }

    @Override
    public GridCoverage convert(Data source, Map<String, Object> params) throws UnconvertibleObjectException {

        try {
            if (params.get(ENCODING) != null && params.get(ENCODING).equals(WPSEncoding.BASE64.getValue())) {
                final List<Object> data = source.getContent();
                if (data.size() != 1) {
                    throw new UnconvertibleObjectException("Only one object in Complex content.");
                }
                final String encodedImage = (String) data.get(0);
                final byte[] byteData = Base64.getDecoder().decode(encodedImage);
                if (byteData != null && byteData.length > 0) {
                    if (WPSMimeType.IMG_GEOTIFF.val().equals(source.getMimeType()) || WPSMimeType.IMG_GEOTIFF_BIS.val().equals(source.getMimeType())) {
                        return readGeotiff(byteData);
                    }
                    return readAnyCoverage(byteData);
                }
                throw new UnconvertibleObjectException("Error during base64 decoding.");
            } else {
                throw new UnconvertibleObjectException("Encoding should be in \"base64\"");
            }
        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }
    }

    public static GridCoverage readGeotiff(byte[] data) throws DataStoreException {
        try (DataStore store = DataStores.open(ByteBuffer.wrap(data), "GeoTIFF")) {
            final Collection<GridCoverageResource> resources = org.geotoolkit.storage.DataStores.flatten(store, true, GridCoverageResource.class);
            final GridCoverageResource gcr = resources.iterator().next();
            return gcr.read(null);
        }
    }

    public static GridCoverage readAnyCoverage(byte[] data) throws DataStoreException {
        try (DataStore store = DataStores.open(ByteBuffer.wrap(data))) {
            final Collection<GridCoverageResource> resources = org.geotoolkit.storage.DataStores.flatten(store, true, GridCoverageResource.class);
            final GridCoverageResource gcr = resources.iterator().next();
            return gcr.read(null);
        }
    }

    /**
     * @todo the resource store is still open but since the data is in memory it should be okay.
     */
    public static GridCoverageResource readGeotiffResource(byte[] data) throws DataStoreException {
        final DataStore store = DataStores.open(ByteBuffer.wrap(data), "GeoTIFF");
        final Collection<GridCoverageResource> resources = org.geotoolkit.storage.DataStores.flatten(store, true, GridCoverageResource.class);
        return resources.iterator().next();
    }

    /**
     * @todo the resource store is still open but since the data is in memory it should be okay.
     */
    public static GridCoverageResource readAnyCoverageResource(byte[] data) throws DataStoreException {
        final DataStore store = DataStores.open(ByteBuffer.wrap(data));
        final Collection<GridCoverageResource> resources = org.geotoolkit.storage.DataStores.flatten(store, true, GridCoverageResource.class);
        return resources.iterator().next();
    }
}
