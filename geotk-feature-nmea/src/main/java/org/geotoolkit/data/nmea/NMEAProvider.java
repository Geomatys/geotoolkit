/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.nmea;

import java.net.URI;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.locationtech.jts.geom.Point;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * NMEA data store provider.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = NMEAProvider.NAME,
        fileSuffixes = {"txt","log"},
        capabilities = {Capability.READ},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={ Point.class })
public class NMEAProvider extends DataStoreProvider {

    public static final String NAME = "NMEA";
    public static final String MIME_TYPE = "application/x-nmea";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).createGroup(PATH);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        if  (ProbeResult.SUPPORTED.equals(connector.pathEndsWith(".txt", true))
          || ProbeResult.SUPPORTED.equals(connector.pathEndsWith(".log", true))
          || ProbeResult.SUPPORTED.equals(connector.contentStartsWith(new byte[]{'$'}))
                ) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public NMEAStore open(final ParameterValueGroup params) throws DataStoreException {
        return new NMEAStore(params);
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.commit(URI.class, NAME);
        final Parameters parameters = Parameters.castOrWrap(PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(PATH).setValue(uri);
        return open(parameters);
    }

}
