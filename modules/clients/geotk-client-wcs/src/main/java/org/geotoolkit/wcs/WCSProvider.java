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
package org.geotoolkit.wcs;

import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.opengis.parameter.*;

/**
 * Web Coverage Service Server factory.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
@StoreMetadata(
        formatName = WCSProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {})
@StoreMetadataExt(resourceTypes = ResourceType.COVERAGE)
public class WCSProvider extends AbstractClientProvider{

    /** factory identification **/
    public static final String NAME = "wcs";

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<String> VERSION;
    static{
        final WCSVersion[] values = WCSVersion.values();
        final String[] validValues =  new String[values.length];
        for(int i=0;i<values.length;i++){
            validValues[i] = values[i].getCode();
        }
        VERSION = createVersionDescriptor(validValues, WCSVersion.v111.getCode());
    }

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName(NAME).addName("WCSParameters").createGroup(URL,VERSION,SECURITY,TIMEOUT);

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
    public WebCoverageClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new WebCoverageClient(params);
    }

}
