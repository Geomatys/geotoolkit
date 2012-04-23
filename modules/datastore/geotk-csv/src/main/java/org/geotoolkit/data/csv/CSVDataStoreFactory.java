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
import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * CSV datastore factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVDataStoreFactory extends AbstractFileDataStoreFactory {

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
    
    public static final ParameterDescriptor<String> IDENTIFIER = new DefaultParameterDescriptor<String>(
                    AbstractDataStoreFactory.IDENTIFIER.getName().getCode(),
                    AbstractDataStoreFactory.IDENTIFIER.getRemarks(), String.class,NAME,true);
    
    /**
     * Optional - the separator character
     */
    public static final ParameterDescriptor<Character> SEPARATOR =
            new DefaultParameterDescriptor<Character>("separator","spécify the separator",Character.class,';',false);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("CSVParameters",
                IDENTIFIER,URLP,NAMESPACE,SEPARATOR);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public String getDescription() {
        return "CSV files (*.csv)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        return new CSVDataStore(params);
    }

    @Override
    public DataStore createNew(final ParameterValueGroup params) throws DataStoreException {
        return create(params);
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {".csv"};
    }

}
