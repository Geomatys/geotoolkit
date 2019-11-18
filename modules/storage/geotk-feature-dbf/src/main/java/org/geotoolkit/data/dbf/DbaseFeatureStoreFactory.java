/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.data.dbf;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.feature.AbstractFileFeatureStoreFactory;
import org.geotoolkit.storage.feature.FileFeatureStoreFactory;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * DBF featurestore factory. handle only reading actually.
 * Todo : handle feature writer.
 *
 * @author Johann Sorel
 * @module
 */
@StoreMetadata(
        formatName = DbaseFeatureStoreFactory.NAME,
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(resourceTypes = ResourceType.VECTOR)
public class DbaseFeatureStoreFactory extends AbstractFileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "dbf";
    public static final String MIME_TYPE = "application/dbase";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder()
                    .addName(NAME).addName("DBFParameters")
                    .createGroup(IDENTIFIER, PATH);

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.databaseDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.databaseTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DbaseFileFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new DbaseFileFeatureStore(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DbaseFileFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("dbf");
    }

    @Override
    public Collection<byte[]> getSignature() {
        return Collections.singleton(new byte[]{0x03});
    }

}
