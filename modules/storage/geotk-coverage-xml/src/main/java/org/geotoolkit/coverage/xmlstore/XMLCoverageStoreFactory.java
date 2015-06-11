/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLCoverageStoreFactory extends AbstractCoverageStoreFactory {

    /** factory identification **/
    public static final String NAME = "coverage-xml-pyramid";
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
     * Mandatory - the folder path
     */
    public static final ParameterDescriptor<URL> PATH;
    /**
     * A parameter to specify if tile states will be checked using descriptor file (default) or not.
     */
    public static final ParameterDescriptor<Boolean> CACHE_TILE_STATE;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR;
    static {
        ParameterBuilder builder = new ParameterBuilder();
        PATH = builder.addName("path")
                .setRemarks(new ResourceInternationalString("org/geotoolkit/coverage/xmlstore/bundle", "coverageXMLPathRemarks"))
                .setRequired(true).create(URL.class, null);
        CACHE_TILE_STATE = builder.addName("cacheTileState")
                .setRemarks(new ResourceInternationalString("org/geotoolkit/coverage/xmlstore/bundle", "coverageXMLTileStateRemarks"))
                .setRequired(false).create(Boolean.class, false);
        PARAMETERS_DESCRIPTOR = builder.addName("XMLCoverageStoreParameters").createGroup(IDENTIFIER, PATH, NAMESPACE, CACHE_TILE_STATE);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/coverage/xmlstore/bundle", "coverageXMLDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/coverage/xmlstore/bundle", "coverageXMLTitle");
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public CoverageStore open(ParameterValueGroup params) throws DataStoreException {
        if(!canProcess(params)){
            throw new DataStoreException("Can not process parameters.");
        }
        try {
            return new XMLCoverageStore(params);
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.PYRAMID, true, true, true);
    }
}
