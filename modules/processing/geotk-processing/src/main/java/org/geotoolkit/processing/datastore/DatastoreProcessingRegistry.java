/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.datastore;

import java.util.Collections;

import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.datastore.copy.CopyDescriptor;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Factory for featurestore process
 * Factory name : "datastore"
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DatastoreProcessingRegistry extends AbstractProcessingRegistry {

    /**Factory name*/
    public static final String NAME = "datastore";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    /**
     * Default constructor 
     */
    public DatastoreProcessingRegistry() {
        super(CopyDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
}
