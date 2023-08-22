/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.data.osm.client;

import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.*;

/**
 * Open Street Map Server factory.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
@StoreMetadata(
        formatName = OSMProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {})
@StoreMetadataExt(resourceTypes = ResourceType.OTHER)
public class OSMProvider extends AbstractClientProvider {

    /** factory identification **/
    public static final String NAME = "osm";

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<String> VERSION;
    static{
        final OSMVersion[] values = OSMVersion.values();
        final String[] validValues =  new String[values.length];
        for(int i=0;i<values.length;i++){
            validValues[i] = values[i].getCode();
        }
        VERSION = createVersionDescriptor(validValues, OSMVersion.v060.getCode());
    }

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("OSMParameters").createGroup(URL,VERSION,SECURITY,TIMEOUT);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.serverDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.serverTitle);
    }

    @Override
    public OpenStreetMapClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new OpenStreetMapClient(params);
    }

}
