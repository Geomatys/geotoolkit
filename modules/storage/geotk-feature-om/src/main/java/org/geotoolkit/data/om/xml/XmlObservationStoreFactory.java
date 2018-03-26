/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.data.om.xml;

import java.io.IOException;
import java.net.URI;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.AbstractObservationStoreFactory;
import org.geotoolkit.observation.Bundle;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@StoreMetadataExt(resourceTypes = ResourceType.SENSOR, canCreate = true, canWrite = true)
public class XmlObservationStoreFactory extends AbstractObservationStoreFactory {

    /** factory identification **/
    public static final String NAME = "observationXmlFile";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    /**
     * url to the file.
     */
    public static final ParameterDescriptor<URI> FILE_PATH =  new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.paramURLAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramURLRemarks))
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("ObservationXmlFileParameters").createGroup(
                IDENTIFIER,NAMESPACE,FILE_PATH);

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public XmlObservationStore open(ParameterValueGroup params) throws DataStoreException {
        try {
            return new XmlObservationStore(params);
        } catch (IOException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public XmlObservationStore create(ParameterValueGroup params) throws DataStoreException {
        try {
            return new XmlObservationStore(params);
        } catch (IOException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        }
    }

}
