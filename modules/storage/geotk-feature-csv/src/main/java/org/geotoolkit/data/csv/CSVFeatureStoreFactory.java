/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.Collections;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;

/**
 * CSV featurestore factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CSVFeatureStoreFactory extends AbstractFileFeatureStoreFactory {

    
    
    
    /** factory identification **/
    public static final String NAME = "csv";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }
    
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);
    
    /**
     * Optional - the separator character
     */
    public static final ParameterDescriptor<Character> SEPARATOR = new ParameterBuilder()
            .addName("separator")
            .addName(Bundle.formatInternational(Bundle.Keys.paramSeparatorAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramSeparatorRemarks))
            .setRequired(false)
            .create(Character.class, ';');

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("CSVParameters").createGroup(
                IDENTIFIER, PATH,NAMESPACE,SEPARATOR);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public CSVFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new CSVFeatureStore(params);
    }

    @Override
    public CSVFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {".csv"};
    }
 
    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, true, true, false, GEOMS_ALL);
    }
    
}
