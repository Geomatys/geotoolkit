/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2019, Geomatys
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

package org.geotoolkit.data.csv;

import java.net.URI;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * CSV Provider.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = CSVProvider.NAME,
        fileSuffixes = {"csv","txt"},
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {FeatureSet.class})
public class CSVProvider extends DataStoreProvider {

    public static final String NAME = "geotk_csv";
    public static final String MIME_TYPE = "text/csv";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Optional - the separator character
     */
    public static final ParameterDescriptor<Character> SEPARATOR = new ParameterBuilder()
            .addName("separator")
            .addName(Bundle.formatInternational(Bundle.Keys.paramSeparatorAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramSeparatorRemarks))
            .setRequired(false)
            .create(Character.class, ';');

    /**
     * Optional - latitude column
     */
    public static final ParameterDescriptor<String> LAT_COLUMN = new ParameterBuilder()
            .addName("lat_column")
            .addName(Bundle.formatInternational(Bundle.Keys.paramLatColumnAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramLatColumnRemarks))
            .setRequired(false)
            .create(String.class, null);

    /**
     * Optional - latitude column
     */
    public static final ParameterDescriptor<String> LON_COLUMN = new ParameterBuilder()
            .addName("lon_column")
            .addName(Bundle.formatInternational(Bundle.Keys.paramLonColumnAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramLonColumnRemarks))
            .setRequired(false)
            .create(String.class, null);

    /**
     * Optional - Coordinate reference system
     */
    public static final ParameterDescriptor<String> CRS = new ParameterBuilder()
            .addName("crs")
            .addName(Bundle.formatInternational(Bundle.Keys.paramCrsAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramCrsRemarks))
            .setRequired(false)
            .create(String.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR
            = new ParameterBuilder().addName(NAME).createGroup(PATH, SEPARATOR, LAT_COLUMN, LON_COLUMN, CRS);

    @Override
    public String getShortName() {
        return "CSV";
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        if  (ProbeResult.SUPPORTED.equals(connector.pathEndsWith(".csv", true))
           ||ProbeResult.SUPPORTED.equals(connector.pathEndsWith(".txt", true))
                ) {
            return new ProbeResult(true, MIME_TYPE, null);
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public CSVStore open(final ParameterValueGroup params) throws DataStoreException {
        return new CSVStore(params);
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.commit(URI.class, NAME);
        final Parameters parameters = Parameters.castOrWrap(PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(PATH).setValue(uri);
        return open(parameters);
    }

}
