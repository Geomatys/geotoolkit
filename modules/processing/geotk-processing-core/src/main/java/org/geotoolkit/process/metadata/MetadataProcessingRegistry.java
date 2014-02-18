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
package org.geotoolkit.process.metadata;

import java.util.Collections;

import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessingRegistry;
import org.geotoolkit.process.metadata.merge.MergeDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Factory for metadata process
 * Factory name : "metadata"
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MetadataProcessingRegistry extends AbstractProcessingRegistry {

    /**Factory name*/
    public static final String NAME = "metadata";
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
    public MetadataProcessingRegistry() {
        super(MergeDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
}
