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

import java.util.Collections;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ResourceInternationalString;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * DBF datastore factory. handle only reading actually.
 * Todo : handle feature writer.
 *
 * @author Johann Sorel
 * @module pending
 */
public class DbaseDataStoreFactory extends AbstractFileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "dbf";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("DBFParameters",
                IDENTIFIER,URLP,NAMESPACE);

    /**
     * {@inheritDoc}
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/dbf/bundle", "databaseDescription");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/dbf/bundle", "databaseTitle");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        return new DbaseFileDataStore(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getFileExtensions() {
        return new String[] {".dbf"};
    }

}
